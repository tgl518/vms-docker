package cn.yznu.vms.user.service.impl;

import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.user.dto.PermissionCreateDTO;
import cn.yznu.vms.user.dto.PermissionUpdateDTO;
import cn.yznu.vms.user.entity.Permission;
import cn.yznu.vms.user.mapper.PermissionMapper;
import cn.yznu.vms.user.service.PermissionService;
import cn.yznu.vms.user.vo.PermissionVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    @Cacheable(value = "userPermissions", key = "#userId", unless = "#result.isEmpty()")
    public List<PermissionVO> getUserPermissions(Long userId) {
        // 1. 获取用户所有权限（扁平列表）
        List<Permission> permissions = permissionMapper.selectByUserId(userId);
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 转换为VO并构建树形结构
        return buildTree(permissions);
    }

    @Override
    public List<String> getUserPermissionCodes(Long userId) {
        return permissionMapper.selectCodesByUserId(userId);
    }

    @Override
    @Cacheable(value = "permissionTree", unless = "#result.isEmpty()")
    public List<PermissionVO> getAllPermissionTree() {
        // 查询所有权限（包括未启用的）
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Permission::getSort);
        List<Permission> permissions = permissionMapper.selectList(wrapper);
        
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyList();
        }
        
        return buildTreeWithId(permissions);
    }

    @Override
    @Cacheable(value = "menuTree", unless = "#result.isEmpty()")
    public List<PermissionVO> getMenuTree() {
        // 只查询MENU类型且启用的权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getType, "MENU")
               .eq(Permission::getIsEnable, 1)
               .orderByAsc(Permission::getSort);
        List<Permission> permissions = permissionMapper.selectList(wrapper);
        
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyList();
        }
        
        return buildTreeWithId(permissions);
    }

    @Override
    public List<PermissionVO> getButtonsByParentId(Long parentId) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, parentId)
               .eq(Permission::getType, "BUTTON")
               .orderByAsc(Permission::getSort);
        List<Permission> permissions = permissionMapper.selectList(wrapper);
        
        return permissions.stream()
                .map(this::convertToVOWithId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"userPermissions", "permissionTree", "menuTree"}, allEntries = true)
    public Long createPermission(PermissionCreateDTO dto) {
        // 检查编码是否重复
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getCode, dto.getCode());
        if (permissionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("权限编码已存在");
        }
        
        Permission permission = new Permission();
        permission.setCode(dto.getCode());
        permission.setName(dto.getName());
        permission.setType(dto.getType() != null ? dto.getType() : "MENU");
        permission.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        permission.setPath(dto.getPath());
        permission.setComponent(dto.getComponent());
        permission.setIcon(dto.getIcon());
        permission.setSort(dto.getOrder() != null ? dto.getOrder() : 0);
        permission.setIsShow(dto.getShow() != null && dto.getShow() ? 1 : 0);
        permission.setIsEnable(dto.getEnable() != null && dto.getEnable() ? 1 : 0);
        permission.setKeepAlive(dto.getKeepAlive() != null && dto.getKeepAlive() ? 1 : 0);
        permission.setLayout(dto.getLayout());
        permission.setRedirect(dto.getRedirect());
        
        permissionMapper.insert(permission);
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"userPermissions", "permissionTree", "menuTree"}, allEntries = true)
    public void updatePermission(Long id, PermissionUpdateDTO dto) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (StringUtils.hasText(dto.getName())) {
            permission.setName(dto.getName());
        }
        if (dto.getType() != null) {
            permission.setType(dto.getType());
        }
        if (dto.getParentId() != null) {
            permission.setParentId(dto.getParentId());
        }
        if (dto.getPath() != null) {
            permission.setPath(dto.getPath());
        }
        if (dto.getComponent() != null) {
            permission.setComponent(dto.getComponent());
        }
        if (dto.getIcon() != null) {
            permission.setIcon(dto.getIcon());
        }
        if (dto.getOrder() != null) {
            permission.setSort(dto.getOrder());
        }
        if (dto.getShow() != null) {
            permission.setIsShow(dto.getShow() ? 1 : 0);
        }
        if (dto.getEnable() != null) {
            permission.setIsEnable(dto.getEnable() ? 1 : 0);
        }
        if (dto.getKeepAlive() != null) {
            permission.setKeepAlive(dto.getKeepAlive() ? 1 : 0);
        }
        if (dto.getLayout() != null) {
            permission.setLayout(dto.getLayout());
        }
        if (dto.getRedirect() != null) {
            permission.setRedirect(dto.getRedirect());
        }
        
        permissionMapper.updateById(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"userPermissions", "permissionTree", "menuTree"}, allEntries = true)
    public void deletePermission(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        // 检查是否有子权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, id);
        if (permissionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该权限存在子权限，请先删除子权限");
        }
        
        permissionMapper.deleteById(id);
    }

    @Override
    @CacheEvict(value = {"userPermissions", "permissionTree", "menuTree"}, allEntries = true)
    public void clearCache() {
        // 仅清除缓存，无其他操作
    }

    /**
     * 构建树形结构（用于用户权限，不包含id）
     */
    private List<PermissionVO> buildTree(List<Permission> permissions) {
        Map<Long, PermissionVO> voMap = new HashMap<>();
        List<PermissionVO> rootList = new ArrayList<>();

        for (Permission p : permissions) {
            PermissionVO vo = convertToVO(p);
            voMap.put(p.getId(), vo);
        }

        for (Permission p : permissions) {
            PermissionVO vo = voMap.get(p.getId());
            if (p.getParentId() == null || p.getParentId() == 0) {
                rootList.add(vo);
            } else {
                PermissionVO parent = voMap.get(p.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                } else {
                    rootList.add(vo);
                }
            }
        }

        sortPermissions(rootList);
        return rootList;
    }

    /**
     * 构建树形结构（包含id，用于权限管理）
     */
    private List<PermissionVO> buildTreeWithId(List<Permission> permissions) {
        Map<Long, PermissionVO> voMap = new HashMap<>();
        List<PermissionVO> rootList = new ArrayList<>();

        for (Permission p : permissions) {
            PermissionVO vo = convertToVOWithId(p);
            voMap.put(p.getId(), vo);
        }

        for (Permission p : permissions) {
            PermissionVO vo = voMap.get(p.getId());
            if (p.getParentId() == null || p.getParentId() == 0) {
                rootList.add(vo);
            } else {
                PermissionVO parent = voMap.get(p.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                } else {
                    rootList.add(vo);
                }
            }
        }

        sortPermissions(rootList);
        return rootList;
    }

    /**
     * Permission 转 PermissionVO（不含id）
     */
    private PermissionVO convertToVO(Permission p) {
        PermissionVO vo = new PermissionVO();
        vo.setCode(p.getCode());
        vo.setName(p.getName());
        vo.setType(p.getType());
        vo.setPath(p.getPath());
        vo.setComponent(p.getComponent());
        vo.setIcon(p.getIcon());
        vo.setOrder(p.getSort());
        vo.setShow(p.getIsShow() != null && p.getIsShow() == 1);
        vo.setEnable(p.getIsEnable() != null && p.getIsEnable() == 1);
        vo.setKeepAlive(p.getKeepAlive() != null && p.getKeepAlive() == 1);
        vo.setLayout(p.getLayout());
        vo.setRedirect(p.getRedirect());
        return vo;
    }

    /**
     * Permission 转 PermissionVO（含id，用于管理）
     */
    private PermissionVO convertToVOWithId(Permission p) {
        PermissionVO vo = convertToVO(p);
        vo.setId(p.getId());
        return vo;
    }

    /**
     * 递归排序权限树
     */
    private void sortPermissions(List<PermissionVO> list) {
        if (list == null || list.isEmpty()) return;

        list.sort(Comparator.comparingInt(vo -> vo.getOrder() != null ? vo.getOrder() : 0));

        for (PermissionVO vo : list) {
            if (vo.getChildren() != null && !vo.getChildren().isEmpty()) {
                sortPermissions(vo.getChildren());
            }
        }
    }
}

