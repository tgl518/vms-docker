package cn.yznu.vms.user.service.impl;

import cn.yznu.vms.common.enums.StatusEnum;
import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.common.utils.JwtUtils;
import cn.yznu.vms.user.dto.UserInfoUpdateDTO;
import cn.yznu.vms.user.dto.UserLoginDTO;
import cn.yznu.vms.user.dto.UserRegisterDTO;
import cn.yznu.vms.user.entity.User;
import cn.yznu.vms.user.entity.UserInfo;
import cn.yznu.vms.user.entity.UserRole;
import cn.yznu.vms.user.entity.Role;
import cn.yznu.vms.user.mapper.PermissionMapper;
import cn.yznu.vms.user.mapper.RoleMapper;
import cn.yznu.vms.user.mapper.UserInfoMapper;
import cn.yznu.vms.user.mapper.UserMapper;
import cn.yznu.vms.user.mapper.UserRoleMapper;
import cn.yznu.vms.user.service.UserService;
import cn.yznu.vms.user.vo.LoginVO;
import cn.yznu.vms.user.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserInfoMapper userInfoMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PermissionMapper permissionMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    // 默认角色ID: 普通用户
    private static final Long DEFAULT_ROLE_ID = 2L;

    @Override
    public LoginVO login(UserLoginDTO dto) {
        // 1. 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, dto.getUsername())
        );

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 3. 校验账号状态
        if (user.getStatus() == null || !user.getStatus().isEnabled()) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 4. 查询用户档案
        UserInfo userInfo = userInfoMapper.selectById(user.getId());

        // 5. 查询用户角色列表 (RBAC)
        java.util.List<String> roles = roleMapper.selectCodesByUserId(user.getId());
        if (roles == null || roles.isEmpty()) {
            roles = java.util.Collections.singletonList("user");
        }

        // 6. 生成 Token
        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), roles);

        // 7. 组装响应
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(userInfo != null ? userInfo.getNickname() : user.getUsername());
        vo.setAvatar(userInfo != null ? userInfo.getAvatar() : null);
        vo.setRoles(roles);
        // 获取权限列表
        vo.setPermissions(permissionMapper.selectCodesByUserId(user.getId()));
        vo.setAccessToken(token);

        log.info("用户登录成功: {}", user.getUsername());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterDTO dto) {
        // 1. 校验密码一致性
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        // 2. 检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 3. 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(StatusEnum.ENABLED);  // 默认启用
        userMapper.insert(user);

        // 4. 创建用户档案
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        userInfo.setGender((byte) 2);  // 默认保密
        userInfoMapper.insert(userInfo);

        // 5. 分配默认角色 (RBAC)
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(DEFAULT_ROLE_ID);
        userRoleMapper.insert(userRole);

        log.info("用户注册成功: {}", user.getUsername());
        return user.getId();
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 查询用户角色列表 (RBAC)
        java.util.List<String> roles = roleMapper.selectCodesByUserId(userId);

        UserVO vo = new UserVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setRoles(roles);
        // 获取权限列表
        vo.setPermissions(permissionMapper.selectCodesByUserId(userId));
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());

        if (userInfo != null) {
            vo.setNickname(userInfo.getNickname());
            vo.setAvatar(userInfo.getAvatar());
            vo.setGender(userInfo.getGender());
            vo.setIntro(userInfo.getIntro());
            vo.setBirthday(userInfo.getBirthday());
            vo.setLocation(userInfo.getLocation());
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(Long userId, UserInfoUpdateDTO dto) {
        // 确保用户存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 更新或创建用户档案
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setUserId(userId);
            BeanUtils.copyProperties(dto, userInfo);
            userInfoMapper.insert(userInfo);
        } else {
            if (dto.getNickname() != null) userInfo.setNickname(dto.getNickname());
            if (dto.getAvatar() != null) userInfo.setAvatar(dto.getAvatar());
            if (dto.getGender() != null) userInfo.setGender(dto.getGender());
            if (dto.getIntro() != null) userInfo.setIntro(dto.getIntro());
            if (dto.getBirthday() != null) userInfo.setBirthday(dto.getBirthday());
            if (dto.getLocation() != null) userInfo.setLocation(dto.getLocation());
            userInfoMapper.updateById(userInfo);
        }

        log.info("用户信息更新成功: userId={}", userId);
    }

    @Override
    public UserVO getByUsername(String username) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );
        if (user == null) {
            return null;
        }
        return getUserInfo(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 校验旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "原密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);

        log.info("用户密码修改成功: userId={}", userId);
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<UserVO> listUsers(Integer pageNum, Integer pageSize, cn.yznu.vms.user.dto.UserQueryDTO queryDTO) {
        // 构建分页参数
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO != null) {
            if (queryDTO.getUsername() != null && !queryDTO.getUsername().isEmpty()) {
                wrapper.like(User::getUsername, queryDTO.getUsername());
            }
            if (queryDTO.getEmail() != null && !queryDTO.getEmail().isEmpty()) {
                wrapper.eq(User::getEmail, queryDTO.getEmail());
            }
            // 角色筛选暂不支持（需联表查询）
            if (queryDTO.getStatus() != null) {
                wrapper.eq(User::getStatus, queryDTO.getStatus() == 1 ? StatusEnum.ENABLED : StatusEnum.DISABLED);
            }
        }

        // 查询用户列表
        com.baomidou.mybatisplus.core.metadata.IPage<User> userPage = userMapper.selectPage(page, wrapper);

        // 转换为VO
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserVO> voPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize, userPage.getTotal());

        java.util.List<User> users = userPage.getRecords();
        if (users.isEmpty()) {
            voPage.setRecords(java.util.Collections.emptyList());
            return voPage;
        }

        // ============ 批量查询优化 (解决 N+1 问题) ============
        java.util.List<Long> userIds = users.stream()
                .map(User::getId)
                .collect(java.util.stream.Collectors.toList());

        // 批量查询用户档案 (1 次 SQL 代替 N 次)
        java.util.List<UserInfo> userInfoList = userInfoMapper.selectByUserIds(userIds);
        java.util.Map<Long, UserInfo> userInfoMap = userInfoList.stream()
                .collect(java.util.stream.Collectors.toMap(
                        UserInfo::getUserId,
                        info -> info,
                        (a, b) -> a  // 如果有重复，保留第一个
                ));

        // 批量查询用户角色 (1 次 SQL 代替 N 次)
        java.util.List<java.util.Map<String, Object>> roleResults = roleMapper.selectCodesByUserIds(userIds);
        java.util.Map<Long, java.util.List<String>> rolesMap = roleResults.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        map -> ((Number) map.get("userId")).longValue(),
                        java.util.stream.Collectors.mapping(
                                map -> (String) map.get("roleCode"),
                                java.util.stream.Collectors.toList()
                        )
                ));

        // 组装 VO (纯内存操作，无数据库查询)
        java.util.List<UserVO> voList = users.stream()
                .map(user -> {
                    UserInfo userInfo = userInfoMap.get(user.getId());
                    java.util.List<String> roles = rolesMap.getOrDefault(user.getId(), java.util.Collections.emptyList());
                    UserVO vo = new UserVO();
                    vo.setUserId(user.getId());
                    vo.setUsername(user.getUsername());
                    vo.setEmail(user.getEmail());
                    vo.setPhone(user.getPhone());
                    vo.setRoles(roles);
                    vo.setStatus(user.getStatus());
                    vo.setCreateTime(user.getCreateTime());
                    if (userInfo != null) {
                        vo.setNickname(userInfo.getNickname());
                        vo.setAvatar(userInfo.getAvatar());
                        vo.setGender(userInfo.getGender());
                        vo.setIntro(userInfo.getIntro());
                        vo.setBirthday(userInfo.getBirthday());
                        vo.setLocation(userInfo.getLocation());
                    }
                    return vo;
                })
                .collect(java.util.stream.Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(cn.yznu.vms.user.dto.UserCreateDTO dto) {
        // 检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(dto.getStatus() != null && dto.getStatus() == 1 ? StatusEnum.ENABLED : StatusEnum.DISABLED);
        userMapper.insert(user);

        // 创建用户档案
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNickname(dto.getUsername());
        userInfo.setGender((byte) 2);
        userInfoMapper.insert(userInfo);

        // 分配角色 (RBAC): SUPER_ADMIN=1, admin=3, user=2
        Long roleId = getRoleIdByCode(dto.getRole());
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);

        log.info("管理员创建用户成功: {}", user.getUsername());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(cn.yznu.vms.user.dto.UserUpdateDTO dto, Long operatorId) {
        User user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 权限检查：高优先级角色操作低优先级角色
        checkOperationPermission(dto.getId(), operatorId);

        // 更新用户名（需检查唯一性）
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            if (!dto.getUsername().equals(user.getUsername())) {
                Long count = userMapper.selectCount(
                        new LambdaQueryWrapper<User>()
                                .eq(User::getUsername, dto.getUsername())
                                .ne(User::getId, dto.getId())
                );
                if (count > 0) {
                    throw new BusinessException("用户名已存在");
                }
                user.setUsername(dto.getUsername());
            }
        }

        // 更新基本信息
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus() == 1 ? StatusEnum.ENABLED : StatusEnum.DISABLED);
        }
        userMapper.updateById(user);

        // 更新用户档案（昵称、头像）
        if (dto.getNickname() != null || dto.getAvatar() != null) {
            UserInfo userInfo = userInfoMapper.selectById(dto.getId());
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setUserId(dto.getId());
                userInfo.setNickname(dto.getNickname() != null ? dto.getNickname() : user.getUsername());
                userInfo.setAvatar(dto.getAvatar());
                userInfo.setGender((byte) 2);
                userInfoMapper.insert(userInfo);
            } else {
                if (dto.getNickname() != null) {
                    userInfo.setNickname(dto.getNickname());
                }
                if (dto.getAvatar() != null) {
                    userInfo.setAvatar(dto.getAvatar());
                }
                userInfoMapper.updateById(userInfo);
            }
        }

        // 更新角色 (RBAC)
        if (dto.getRole() != null) {
            userRoleMapper.deleteByUserId(dto.getId());
            Long roleId = getRoleIdByCode(dto.getRole());
            UserRole userRole = new UserRole();
            userRole.setUserId(dto.getId());
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }

        log.info("管理员更新用户成功: userId={}", dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId, Long operatorId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 权限检查：高优先级角色操作低优先级角色
        checkOperationPermission(userId, operatorId);

        // 删除用户角色关联 (RBAC)
        userRoleMapper.deleteByUserId(userId);
        // 删除用户档案
        userInfoMapper.deleteById(userId);
        // 删除用户
        userMapper.deleteById(userId);

        log.info("管理员删除用户成功: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, String newPassword, Long operatorId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 权限检查：高优先级角色操作低优先级角色
        checkOperationPermission(userId, operatorId);

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);

        log.info("管理员重置用户密码成功: userId={}", userId);
    }

    @Override
    public Long count() {
        return (long) userMapper.selectCount(null);
    }

    @Override
    public Long getTodayNewUsers() {
        java.time.LocalDateTime todayStart = java.time.LocalDateTime.of(
                java.time.LocalDate.now(), java.time.LocalTime.MIN);
        return (long) userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .ge(User::getCreateTime, todayStart)
        );
    }

    /**
     * 检查操作权限：高优先级的角色才能操作低优先级的角色
     * @param targetUserId 目标用户ID
     * @param operatorId 操作者用户ID
     */
    private void checkOperationPermission(Long targetUserId, Long operatorId) {
        // 1. 获取操作者的角色及最高权重 (sort最小)
        List<Role> operatorRoles = roleMapper.selectByUserId(operatorId);
        if (operatorRoles == null || operatorRoles.isEmpty()) {
            throw new BusinessException("权限不足：无效的操作者角色");
        }
        int minOperatorSort = operatorRoles.stream().mapToInt(Role::getSort).min().orElse(Integer.MAX_VALUE);
        
        // 如果包含超级管理员角色 (sort往往为0)，则拥有最高权限
        boolean isSuperAdmin = operatorRoles.stream().anyMatch(r -> "SUPER_ADMIN".equalsIgnoreCase(r.getCode()));
        if (isSuperAdmin) {
            return;
        }

        // 2. 获取目标用户的最高角色权重
        List<Role> targetRoles = roleMapper.selectByUserId(targetUserId);
        if (targetRoles == null || targetRoles.isEmpty()) {
            return; // 目标无角色，可操作
        }
        int minTargetSort = targetRoles.stream().mapToInt(Role::getSort).min().orElse(Integer.MAX_VALUE);

        // 3. 比较权重：操作者的 sort 必须严格小于目标用户的 sort
        if (minOperatorSort >= minTargetSort) {
            log.warn("越权操作尝试: operatorId={}, targetUserId={}, operatorSort={}, targetSort={}", 
                    operatorId, targetUserId, minOperatorSort, minTargetSort);
            throw new BusinessException("权限不足：您无法操作同级或更高等级的用户");
        }
    }

    /**
     * 根据角色编码获取角色ID
     */
    private Long getRoleIdByCode(String roleCode) {
        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getCode, roleCode));
        if (role != null) {
            return role.getId();
        }
        // 如果找不到，返回默认角色ID (user)
        Role defaultRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getCode, "user"));
        return defaultRole != null ? defaultRole.getId() : 2L;
    }
}
