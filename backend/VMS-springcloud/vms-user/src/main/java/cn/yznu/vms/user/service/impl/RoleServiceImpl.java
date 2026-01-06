package cn.yznu.vms.user.service.impl;

import cn.yznu.vms.common.enums.StatusEnum;
import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.user.dto.RoleCreateDTO;
import cn.yznu.vms.user.dto.RoleUpdateDTO;
import cn.yznu.vms.user.entity.Permission;
import cn.yznu.vms.user.entity.Role;
import cn.yznu.vms.user.entity.RolePermission;
import cn.yznu.vms.user.mapper.PermissionMapper;
import cn.yznu.vms.user.mapper.RoleMapper;
import cn.yznu.vms.user.mapper.RolePermissionMapper;
import cn.yznu.vms.user.mapper.UserRoleMapper;
import cn.yznu.vms.user.service.RoleService;
import cn.yznu.vms.user.vo.RoleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final PermissionMapper permissionMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_ROLE_PERMISSION_PREFIX = "role_permissions:";

    @Override
    public IPage<RoleVO> listRoles(Integer pageNum, Integer pageSize, String name, Boolean enable) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like(Role::getName, name);
        }
        if (enable != null) {
            wrapper.eq(Role::getStatus, enable ? StatusEnum.ENABLED : StatusEnum.DISABLED);
        }
        wrapper.orderByAsc(Role::getSort);
        
        IPage<Role> rolePage = roleMapper.selectPage(page, wrapper);
        List<Role> roles = rolePage.getRecords();
        
        if (roles.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        // 批量查询权限关联，解决 N+1 问题
        List<Long> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
        List<RolePermission> allRelations = rolePermissionMapper.selectByRoleIds(roleIds);
        Map<Long, List<Long>> rolePermissionMap = allRelations.stream()
                .collect(Collectors.groupingBy(
                        RolePermission::getRoleId,
                        Collectors.mapping(RolePermission::getPermissionId, Collectors.toList())
                ));
        
        // 转换为VO并设置权限列表
        return rolePage.convert(role -> {
            RoleVO vo = convertToVO(role);
            vo.setPermissionIds(rolePermissionMap.getOrDefault(role.getId(), Collections.emptyList()));
            return vo;
        });
    }

    @Override
    public RoleVO getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        RoleVO vo = convertToVO(role);
        // 单体查询时手动填充权限列表
        vo.setPermissionIds(rolePermissionMapper.selectPermissionIdsByRoleId(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateDTO dto) {
        // 检查编码是否重复
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, dto.getCode());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色编码 [" + dto.getCode() + "] 已存在");
        }
        
        // 检查名称是否重复
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getName, dto.getName());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色名称 [" + dto.getName() + "] 已存在");
        }
        
        Role role = new Role();
        role.setCode(dto.getCode());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setSort(dto.getSort() != null ? dto.getSort() : 0);
        role.setStatus(dto.getEnable() != null && dto.getEnable() ? StatusEnum.ENABLED : StatusEnum.DISABLED);
        
        roleMapper.insert(role);
        
        // 保存权限关联
        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            rolePermissionMapper.batchInsert(role.getId(), dto.getPermissionIds());
        }
        
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long id, RoleUpdateDTO dto) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        // 超级管理员与基础管理员不可修改
        String code = role.getCode();
        if ("SUPER_ADMIN".equalsIgnoreCase(code) || "admin".equalsIgnoreCase(code)) {
            // 只允许修改权限，不允许禁用
            if (dto.getEnable() != null && !dto.getEnable()) {
                throw new BusinessException("系统内置角色不能被禁用");
            }
            // 防止清空权限
            if (dto.getPermissionIds() != null && dto.getPermissionIds().isEmpty()) {
                throw new BusinessException("系统内置角色必须拥有权限");
            }
        }
        
        if (StringUtils.hasText(dto.getName()) && !dto.getName().equals(role.getName())) {
            // 检查名称是否冲突
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Role::getName, dto.getName());
            wrapper.ne(Role::getId, id);
            if (roleMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("角色名称 [" + dto.getName() + "] 已存在");
            }
            role.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            role.setDescription(dto.getDescription());
        }
        if (dto.getSort() != null) {
            role.setSort(dto.getSort());
        }
        if (dto.getEnable() != null) {
            role.setStatus(dto.getEnable() ? StatusEnum.ENABLED : StatusEnum.DISABLED);
        }
        
        roleMapper.updateById(role);
        
        // 更新权限关联
        if (dto.getPermissionIds() != null) {
            rolePermissionMapper.deleteByRoleId(id);
            if (!dto.getPermissionIds().isEmpty()) {
                rolePermissionMapper.batchInsert(id, dto.getPermissionIds());
            }
            // 同步缓存
            syncRolePermissionCache(role.getCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        // 系统内置角色不可删除
        String code = role.getCode();
        if ("SUPER_ADMIN".equalsIgnoreCase(code) || "admin".equalsIgnoreCase(code)) {
            throw new BusinessException("系统内置角色不能被删除");
        }

        // 检查是否有用户正在使用该角色
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(id);
        if (userIds != null && !userIds.isEmpty()) {
            throw new BusinessException("该角色下仍有 " + userIds.size() + " 名用户，禁止直接删除。请先移除相关用户后再试。");
        }
        
        // 删除角色权限关联
        rolePermissionMapper.deleteByRoleId(id);
        
        // 清理缓存
        redisTemplate.delete(REDIS_ROLE_PERMISSION_PREFIX + role.getCode());
        
        // 删除角色（软删除）
        roleMapper.deleteById(id);
    }

    @Override
    public List<Long> getRoleUserIds(Long roleId) {
        return userRoleMapper.selectUserIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoleUsers(Long roleId, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        userRoleMapper.batchInsert(roleId, userIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRoleUsers(Long roleId, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        userRoleMapper.batchDelete(roleId, userIds);
    }

    @Override
    public void syncRolePermissionCache(String roleCode) {
        String key = REDIS_ROLE_PERMISSION_PREFIX + roleCode;
        redisTemplate.delete(key);

        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getCode, roleCode));
        if (role != null && role.getStatus() == StatusEnum.ENABLED) {
            List<Permission> permissions = permissionMapper.selectByRoleId(role.getId());
            if (permissions != null && !permissions.isEmpty()) {
                Set<String> codes = permissions.stream()
                        .map(Permission::getCode)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toSet());
                if (!codes.isEmpty()) {
                    redisTemplate.opsForSet().add(key, codes.toArray(new String[0]));
                }
            }
        }
        log.info("同步角色权限缓存完成: roleCode={}", roleCode);
    }

    @Override
    public void initRolePermissionCache() {
        log.info("开始初始化所有角色的权限缓存...");
        List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>().eq(Role::getStatus, StatusEnum.ENABLED));
        for (Role role : roles) {
            syncRolePermissionCache(role.getCode());
        }
        log.info("初始化角色权限缓存完成，总计角色数: {}", roles.size());
    }

    /**
     * Role 转 RoleVO
     * 注意：在列表查询中，permissionIds 会被外层逻辑批量覆盖，这里仅作为单体查询的 fallback 或简单转换
     */
    private RoleVO convertToVO(Role role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setCode(role.getCode());
        vo.setName(role.getName());
        vo.setDescription(role.getDescription());
        vo.setSort(role.getSort());
        vo.setEnable(role.getStatus() == StatusEnum.ENABLED);
        return vo;
    }
}
