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
import cn.yznu.vms.user.vo.RoleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RoleServiceImpl 白盒测试
 * 
 * 测试覆盖目标:
 * - 语句覆盖：确保所有语句至少执行一次
 * - 判定覆盖：确保所有分支（if-else）都被测试
 * - 条件覆盖：确保所有条件的真假值都被测试
 * - 路径覆盖：确保所有可能的执行路径都被测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleServiceImpl 角色管理服务白盒测试")
class RoleServiceImplTest {

    // ==================== Mock 依赖 ====================
    @Mock private RoleMapper roleMapper;
    @Mock private RolePermissionMapper rolePermissionMapper;
    @Mock private UserRoleMapper userRoleMapper;
    @Mock private PermissionMapper permissionMapper;
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private SetOperations<String, String> setOperations;

    @InjectMocks
    private RoleServiceImpl roleService;

    // ==================== 测试数据 ====================
    private Role testRole;
    private RoleCreateDTO createDTO;
    private RoleUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试用角色实体
        testRole = new Role();
        testRole.setId(1L);
        testRole.setCode("TEST_ROLE");
        testRole.setName("测试角色");
        testRole.setDescription("这是一个测试角色");
        testRole.setSort(10);
        testRole.setStatus(StatusEnum.ENABLED);

        // 初始化创建 DTO
        createDTO = new RoleCreateDTO();
        createDTO.setCode("NEW_ROLE");
        createDTO.setName("新角色");
        createDTO.setDescription("新角色描述");
        createDTO.setSort(5);
        createDTO.setEnable(true);
        createDTO.setPermissionIds(Arrays.asList(1L, 2L, 3L));

        // 初始化更新 DTO
        updateDTO = new RoleUpdateDTO();
        updateDTO.setName("更新后的角色名");
        updateDTO.setDescription("更新后的描述");
        updateDTO.setSort(20);
        updateDTO.setEnable(true);
    }

    // ==================== 创建角色测试 (createRole) ====================
    @Nested
    @DisplayName("创建角色 (createRole) 测试")
    class CreateRoleTests {

        /**
         * 测试路径1: 正常创建角色（所有校验通过）
         * 覆盖: 语句覆盖、路径覆盖
         */
        @Test
        @DisplayName("创建成功_当编码和名称都不重复")
        void createRole_shouldSucceed_whenCodeAndNameAreUnique() {
            // Arrange: 模拟编码和名称都不重复
            when(roleMapper.selectCount(any())).thenReturn(0L);
            // 模拟 MyBatis-Plus 插入后自动填充 ID
            doAnswer(invocation -> {
                Role role = invocation.getArgument(0);
                role.setId(100L); // 模拟数据库生成的自增 ID
                return 1;
            }).when(roleMapper).insert(any(Role.class));

            // Act: 执行创建
            Long roleId = roleService.createRole(createDTO);

            // Assert: 验证角色被插入并返回ID
            assertThat(roleId).isNotNull();
            assertThat(roleId).isEqualTo(100L);
            
            // 验证角色插入被调用
            ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
            verify(roleMapper, times(1)).insert(roleCaptor.capture());
            Role capturedRole = roleCaptor.getValue();
            assertThat(capturedRole.getCode()).isEqualTo(createDTO.getCode());
            assertThat(capturedRole.getName()).isEqualTo(createDTO.getName());
            assertThat(capturedRole.getStatus()).isEqualTo(StatusEnum.ENABLED);
            
            // 验证权限关联被保存
            verify(rolePermissionMapper, times(1)).batchInsert(any(), eq(createDTO.getPermissionIds()));
        }

        /**
         * 测试路径2: 编码重复时抛出异常
         * 覆盖: 判定覆盖（第一个 if 判断）
         */
        @Test
        @DisplayName("创建失败_当角色编码已存在")
        void createRole_shouldThrowException_whenCodeAlreadyExists() {
            // Arrange: 模拟编码已存在 (第一次查询返回 > 0)
            when(roleMapper.selectCount(any())).thenReturn(1L);

            // Act & Assert: 期望抛出业务异常
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.createRole(createDTO));
            
            assertThat(exception.getMessage()).contains("角色编码");
            assertThat(exception.getMessage()).contains("已存在");
            
            // 验证后续操作未执行
            verify(roleMapper, never()).insert(any(Role.class));
        }

        /**
         * 测试路径3: 名称重复时抛出异常
         * 覆盖: 判定覆盖（第二个 if 判断）、条件覆盖
         */
        @Test
        @DisplayName("创建失败_当角色名称已存在")
        void createRole_shouldThrowException_whenNameAlreadyExists() {
            // Arrange: 编码不重复，但名称重复
            when(roleMapper.selectCount(any()))
                .thenReturn(0L)  // 第一次查询编码：不存在
                .thenReturn(1L); // 第二次查询名称：已存在

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.createRole(createDTO));
            
            assertThat(exception.getMessage()).contains("角色名称");
            assertThat(exception.getMessage()).contains("已存在");
        }

        /**
         * 测试路径4: 不设置权限列表时跳过权限保存
         * 覆盖: 条件覆盖（permissionIds 为 null 或空）
         */
        @Test
        @DisplayName("创建成功_当权限列表为空时跳过权限保存")
        void createRole_shouldNotSavePermissions_whenPermissionIdsIsEmpty() {
            // Arrange
            createDTO.setPermissionIds(null);
            when(roleMapper.selectCount(any())).thenReturn(0L);

            // Act
            roleService.createRole(createDTO);

            // Assert: 权限关联不应被保存
            verify(rolePermissionMapper, never()).batchInsert(any(), any());
        }

        /**
         * 测试路径5: 不设置排序值时使用默认值
         * 覆盖: 条件覆盖（sort 为 null）
         */
        @Test
        @DisplayName("创建成功_当排序为null时使用默认值0")
        void createRole_shouldUseDefaultSort_whenSortIsNull() {
            // Arrange
            createDTO.setSort(null);
            createDTO.setPermissionIds(null);
            when(roleMapper.selectCount(any())).thenReturn(0L);

            // Act
            roleService.createRole(createDTO);

            // Assert: 验证排序值为默认值 0
            ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
            verify(roleMapper).insert(roleCaptor.capture());
            assertThat(roleCaptor.getValue().getSort()).isEqualTo(0);
        }

        /**
         * 测试路径6: enable 为 false 时状态为 DISABLED
         * 覆盖: 条件覆盖（enable 的各种取值）
         */
        @Test
        @DisplayName("创建成功_当enable为false时状态为DISABLED")
        void createRole_shouldSetDisabled_whenEnableIsFalse() {
            // Arrange
            createDTO.setEnable(false);
            createDTO.setPermissionIds(null);
            when(roleMapper.selectCount(any())).thenReturn(0L);

            // Act
            roleService.createRole(createDTO);

            // Assert
            ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
            verify(roleMapper).insert(roleCaptor.capture());
            assertThat(roleCaptor.getValue().getStatus()).isEqualTo(StatusEnum.DISABLED);
        }
    }

    // ==================== 更新角色测试 (updateRole) ====================
    @Nested
    @DisplayName("更新角色 (updateRole) 测试")
    class UpdateRoleTests {

        /**
         * 测试路径1: 正常更新角色
         * 覆盖: 语句覆盖、路径覆盖
         */
        @Test
        @DisplayName("更新成功_当角色存在且参数有效")
        void updateRole_shouldSucceed_whenRoleExistsAndParamsValid() {
            // Arrange
            when(roleMapper.selectById(1L)).thenReturn(testRole);
            when(roleMapper.selectCount(any())).thenReturn(0L);
            // 注意：这里不mock redisTemplate.delete，因为updateDTO没有设置permissionIds，不会触发缓存同步

            // Act
            roleService.updateRole(1L, updateDTO);

            // Assert
            verify(roleMapper, times(1)).updateById(any(Role.class));
        }

        /**
         * 测试路径2: 角色不存在时抛出异常
         * 覆盖: 判定覆盖（role == null）
         */
        @Test
        @DisplayName("更新失败_当角色不存在")
        void updateRole_shouldThrowException_whenRoleNotFound() {
            // Arrange: 模拟角色不存在
            when(roleMapper.selectById(999L)).thenReturn(null);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.updateRole(999L, updateDTO));
            
            assertThat(exception.getCode()).isEqualTo(ResultCode.NOT_FOUND.getCode());
        }

        /**
         * 测试路径3: 尝试禁用系统内置角色 (SUPER_ADMIN)
         * 覆盖: 判定覆盖（内置角色保护逻辑）
         */
        @Test
        @DisplayName("更新失败_当尝试禁用SUPER_ADMIN角色")
        void updateRole_shouldThrowException_whenDisablingSuperAdmin() {
            // Arrange: 设置为 SUPER_ADMIN 角色
            testRole.setCode("SUPER_ADMIN");
            updateDTO.setEnable(false);
            when(roleMapper.selectById(1L)).thenReturn(testRole);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.updateRole(1L, updateDTO));
            
            assertThat(exception.getMessage()).contains("系统内置角色不能被禁用");
        }

        /**
         * 测试路径4: 尝试禁用系统内置角色 (admin)
         * 覆盖: 条件覆盖（admin.equalsIgnoreCase）
         */
        @Test
        @DisplayName("更新失败_当尝试禁用admin角色")
        void updateRole_shouldThrowException_whenDisablingAdmin() {
            // Arrange
            testRole.setCode("admin");
            updateDTO.setEnable(false);
            when(roleMapper.selectById(1L)).thenReturn(testRole);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.updateRole(1L, updateDTO));
            
            assertThat(exception.getMessage()).contains("系统内置角色不能被禁用");
        }

        /**
         * 测试路径5: 尝试清空系统角色权限
         * 覆盖: 条件覆盖（permissionIds 为空列表）
         */
        @Test
        @DisplayName("更新失败_当尝试清空系统角色权限")
        void updateRole_shouldThrowException_whenClearingAdminPermissions() {
            // Arrange
            testRole.setCode("SUPER_ADMIN");
            updateDTO.setEnable(null); // 不修改启用状态
            updateDTO.setPermissionIds(Collections.emptyList()); // 清空权限
            when(roleMapper.selectById(1L)).thenReturn(testRole);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.updateRole(1L, updateDTO));
            
            assertThat(exception.getMessage()).contains("系统内置角色必须拥有权限");
        }

        /**
         * 测试路径6: 更新名称时检测名称冲突
         * 覆盖: 判定覆盖（名称不相同 && 已存在）
         */
        @Test
        @DisplayName("更新失败_当新名称与其他角色重复")
        void updateRole_shouldThrowException_whenNameConflicts() {
            // Arrange
            updateDTO.setName("其他角色名称");
            testRole.setCode("NORMAL_ROLE"); // 非系统角色
            when(roleMapper.selectById(1L)).thenReturn(testRole);
            when(roleMapper.selectCount(any())).thenReturn(1L); // 名称冲突

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.updateRole(1L, updateDTO));
            
            assertThat(exception.getMessage()).contains("角色名称");
            assertThat(exception.getMessage()).contains("已存在");
        }

        /**
         * 测试路径7: 更新权限后同步缓存
         * 覆盖: 路径覆盖（更新权限的完整路径）
         */
        @Test
        @DisplayName("更新成功_当修改权限时同步Redis缓存")
        void updateRole_shouldSyncCache_whenPermissionsUpdated() {
            // Arrange
            testRole.setCode("NORMAL_ROLE");
            updateDTO.setName(null); // 不修改名称
            updateDTO.setPermissionIds(Arrays.asList(1L, 2L));
            
            when(roleMapper.selectById(1L)).thenReturn(testRole);
            when(roleMapper.selectOne(any())).thenReturn(testRole);
            when(redisTemplate.delete(anyString())).thenReturn(true);
            when(redisTemplate.opsForSet()).thenReturn(setOperations);
            
            Permission permission = new Permission();
            permission.setCode("test:permission");
            when(permissionMapper.selectByRoleId(1L)).thenReturn(Collections.singletonList(permission));

            // Act
            roleService.updateRole(1L, updateDTO);

            // Assert: 验证权限被更新并同步缓存
            verify(rolePermissionMapper).deleteByRoleId(1L);
            verify(rolePermissionMapper).batchInsert(1L, updateDTO.getPermissionIds());
            verify(redisTemplate, atLeastOnce()).delete(anyString());
        }
    }

    // ==================== 删除角色测试 (deleteRole) ====================
    @Nested
    @DisplayName("删除角色 (deleteRole) 测试")
    class DeleteRoleTests {

        /**
         * 测试路径1: 正常删除角色
         * 覆盖: 语句覆盖、路径覆盖
         */
        @Test
        @DisplayName("删除成功_当角色存在且无用户关联")
        void deleteRole_shouldSucceed_whenRoleExistsAndNoUsers() {
            // Arrange
            testRole.setCode("NORMAL_ROLE");
            when(roleMapper.selectById(1L)).thenReturn(testRole);
            when(userRoleMapper.selectUserIdsByRoleId(1L)).thenReturn(Collections.emptyList());
            when(redisTemplate.delete(anyString())).thenReturn(true);

            // Act
            roleService.deleteRole(1L);

            // Assert
            verify(rolePermissionMapper).deleteByRoleId(1L);
            verify(roleMapper).deleteById(1L);
        }

        /**
         * 测试路径2: 角色不存在时抛出异常
         * 覆盖: 判定覆盖（role == null）
         */
        @Test
        @DisplayName("删除失败_当角色不存在")
        void deleteRole_shouldThrowException_whenRoleNotFound() {
            // Arrange
            when(roleMapper.selectById(999L)).thenReturn(null);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.deleteRole(999L));
            
            assertThat(exception.getCode()).isEqualTo(ResultCode.NOT_FOUND.getCode());
        }

        /**
         * 测试路径3: 尝试删除系统角色 (SUPER_ADMIN)
         * 覆盖: 判定覆盖（内置角色保护）
         */
        @Test
        @DisplayName("删除失败_当尝试删除SUPER_ADMIN")
        void deleteRole_shouldThrowException_whenDeletingSuperAdmin() {
            // Arrange
            testRole.setCode("SUPER_ADMIN");
            when(roleMapper.selectById(1L)).thenReturn(testRole);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.deleteRole(1L));
            
            assertThat(exception.getMessage()).contains("系统内置角色不能被删除");
        }

        /**
         * 测试路径4: 尝试删除系统角色 (admin)
         * 覆盖: 条件覆盖（admin 的大小写变体）
         */
        @Test
        @DisplayName("删除失败_当尝试删除admin")
        void deleteRole_shouldThrowException_whenDeletingAdmin() {
            // Arrange
            testRole.setCode("Admin"); // 测试大小写不敏感
            when(roleMapper.selectById(1L)).thenReturn(testRole);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.deleteRole(1L));
            
            assertThat(exception.getMessage()).contains("系统内置角色不能被删除");
        }

        /**
         * 测试路径5: 角色下有用户时禁止删除
         * 覆盖: 判定覆盖（userIds 非空）
         */
        @Test
        @DisplayName("删除失败_当角色下有关联用户")
        void deleteRole_shouldThrowException_whenRoleHasUsers() {
            // Arrange
            testRole.setCode("NORMAL_ROLE");
            when(roleMapper.selectById(1L)).thenReturn(testRole);
            when(userRoleMapper.selectUserIdsByRoleId(1L)).thenReturn(Arrays.asList(1L, 2L, 3L));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.deleteRole(1L));
            
            assertThat(exception.getMessage()).contains("3 名用户");
            assertThat(exception.getMessage()).contains("禁止直接删除");
        }
    }

    // ==================== 查询角色测试 (getRoleById / listRoles) ====================
    @Nested
    @DisplayName("查询角色测试")
    class QueryRoleTests {

        /**
         * 测试路径1: 根据ID查询角色成功
         * 覆盖: 语句覆盖
         */
        @Test
        @DisplayName("查询成功_当角色存在")
        void getRoleById_shouldReturnRole_whenExists() {
            // Arrange
            when(roleMapper.selectById(1L)).thenReturn(testRole);
            when(rolePermissionMapper.selectPermissionIdsByRoleId(1L))
                .thenReturn(Arrays.asList(1L, 2L));

            // Act
            RoleVO result = roleService.getRoleById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testRole.getId());
            assertThat(result.getCode()).isEqualTo(testRole.getCode());
            assertThat(result.getName()).isEqualTo(testRole.getName());
            assertThat(result.getPermissionIds()).hasSize(2);
        }

        /**
         * 测试路径2: 查询不存在的角色
         * 覆盖: 判定覆盖（role == null）
         */
        @Test
        @DisplayName("查询失败_当角色不存在")
        void getRoleById_shouldThrowException_whenNotFound() {
            // Arrange
            when(roleMapper.selectById(999L)).thenReturn(null);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                () -> roleService.getRoleById(999L));
            
            assertThat(exception.getCode()).isEqualTo(ResultCode.NOT_FOUND.getCode());
        }

        /**
         * 测试路径3: 分页查询角色列表（带筛选条件）
         * 覆盖: 条件覆盖（name 有值、enable 有值）
         */
        @Test
        @DisplayName("分页查询_带名称和启用状态筛选")
        void listRoles_shouldFilterByNameAndEnable() {
            // Arrange
            Page<Role> mockPage = new Page<>(1, 10);
            mockPage.setRecords(Collections.singletonList(testRole));
            mockPage.setTotal(1);
            
            when(roleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);
            when(rolePermissionMapper.selectByRoleIds(anyList()))
                .thenReturn(Collections.emptyList());

            // Act
            IPage<RoleVO> result = roleService.listRoles(1, 10, "测试", true);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getName()).isEqualTo("测试角色");
        }

        /**
         * 测试路径4: 分页查询返回空列表
         * 覆盖: 条件覆盖（roles.isEmpty()）
         */
        @Test
        @DisplayName("分页查询_返回空列表")
        void listRoles_shouldReturnEmptyPage_whenNoResults() {
            // Arrange
            Page<Role> emptyPage = new Page<>(1, 10);
            emptyPage.setRecords(Collections.emptyList());
            
            when(roleMapper.selectPage(any(Page.class), any()))
                .thenReturn(emptyPage);

            // Act
            IPage<RoleVO> result = roleService.listRoles(1, 10, null, null);

            // Assert
            assertThat(result.getRecords()).isEmpty();
        }
    }

    // ==================== 缓存同步测试 (syncRolePermissionCache) ====================
    @Nested
    @DisplayName("缓存同步 (syncRolePermissionCache) 测试")
    class CacheSyncTests {

        /**
         * 测试路径1: 角色存在且启用时同步缓存
         * 覆盖: 语句覆盖、路径覆盖
         */
        @Test
        @DisplayName("同步成功_当角色存在且启用且有权限")
        void syncCache_shouldAddToRedis_whenRoleEnabledWithPermissions() {
            // Arrange
            when(redisTemplate.delete(anyString())).thenReturn(true);
            when(roleMapper.selectOne(any())).thenReturn(testRole);
            when(redisTemplate.opsForSet()).thenReturn(setOperations);
            
            Permission permission = new Permission();
            permission.setCode("video:view");
            when(permissionMapper.selectByRoleId(1L)).thenReturn(Collections.singletonList(permission));

            // Act
            roleService.syncRolePermissionCache("TEST_ROLE");

            // Assert: 验证缓存被更新
            verify(setOperations).add(eq("role_permissions:TEST_ROLE"), any(String[].class));
        }

        /**
         * 测试路径2: 角色不存在时只删除缓存
         * 覆盖: 条件覆盖（role == null）
         */
        @Test
        @DisplayName("同步时_角色不存在只清除缓存")
        void syncCache_shouldOnlyDeleteCache_whenRoleNotFound() {
            // Arrange
            when(redisTemplate.delete(anyString())).thenReturn(true);
            when(roleMapper.selectOne(any())).thenReturn(null);

            // Act
            roleService.syncRolePermissionCache("NON_EXISTENT");

            // Assert: 只删除缓存，不添加新缓存
            verify(redisTemplate).delete("role_permissions:NON_EXISTENT");
            verify(redisTemplate, never()).opsForSet();
        }

        /**
         * 测试路径3: 角色被禁用时只删除缓存
         * 覆盖: 条件覆盖（status != ENABLED）
         */
        @Test
        @DisplayName("同步时_角色被禁用只清除缓存")
        void syncCache_shouldOnlyDeleteCache_whenRoleDisabled() {
            // Arrange
            testRole.setStatus(StatusEnum.DISABLED);
            when(redisTemplate.delete(anyString())).thenReturn(true);
            when(roleMapper.selectOne(any())).thenReturn(testRole);

            // Act
            roleService.syncRolePermissionCache("TEST_ROLE");

            // Assert
            verify(redisTemplate).delete("role_permissions:TEST_ROLE");
            verify(redisTemplate, never()).opsForSet();
        }

        /**
         * 测试路径4: 角色无权限时不添加缓存
         * 覆盖: 条件覆盖（permissions 为空）
         */
        @Test
        @DisplayName("同步时_角色无权限不添加缓存")
        void syncCache_shouldNotAddCache_whenNoPermissions() {
            // Arrange
            when(redisTemplate.delete(anyString())).thenReturn(true);
            when(roleMapper.selectOne(any())).thenReturn(testRole);
            when(permissionMapper.selectByRoleId(1L)).thenReturn(Collections.emptyList());

            // Act
            roleService.syncRolePermissionCache("TEST_ROLE");

            // Assert
            verify(redisTemplate, never()).opsForSet();
        }
    }

    // ==================== 用户-角色关联测试 ====================
    @Nested
    @DisplayName("用户-角色关联操作测试")
    class UserRoleTests {

        /**
         * 测试: 获取角色下的用户ID列表
         */
        @Test
        @DisplayName("获取角色用户列表")
        void getRoleUserIds_shouldReturnUserIds() {
            // Arrange
            when(userRoleMapper.selectUserIdsByRoleId(1L)).thenReturn(Arrays.asList(1L, 2L, 3L));

            // Act
            List<Long> result = roleService.getRoleUserIds(1L);

            // Assert
            assertThat(result).containsExactly(1L, 2L, 3L);
        }

        /**
         * 测试: 给角色添加用户（正常情况）
         */
        @Test
        @DisplayName("给角色添加用户_正常情况")
        void addRoleUsers_shouldBatchInsert_whenUserIdsNotEmpty() {
            // Arrange
            List<Long> userIds = Arrays.asList(1L, 2L);

            // Act
            roleService.addRoleUsers(1L, userIds);

            // Assert
            verify(userRoleMapper).batchInsert(1L, userIds);
        }

        /**
         * 测试: 给角色添加用户（空列表时跳过）
         * 覆盖: 条件覆盖（userIds 为 null 或空）
         */
        @Test
        @DisplayName("给角色添加用户_空列表时跳过")
        void addRoleUsers_shouldSkip_whenUserIdsEmpty() {
            // Act
            roleService.addRoleUsers(1L, null);
            roleService.addRoleUsers(1L, Collections.emptyList());

            // Assert
            verify(userRoleMapper, never()).batchInsert(any(), any());
        }

        /**
         * 测试: 从角色移除用户（正常情况）
         */
        @Test
        @DisplayName("从角色移除用户_正常情况")
        void removeRoleUsers_shouldBatchDelete_whenUserIdsNotEmpty() {
            // Arrange
            List<Long> userIds = Arrays.asList(1L, 2L);

            // Act
            roleService.removeRoleUsers(1L, userIds);

            // Assert
            verify(userRoleMapper).batchDelete(1L, userIds);
        }

        /**
         * 测试: 从角色移除用户（空列表时跳过）
         */
        @Test
        @DisplayName("从角色移除用户_空列表时跳过")
        void removeRoleUsers_shouldSkip_whenUserIdsEmpty() {
            // Act
            roleService.removeRoleUsers(1L, null);
            roleService.removeRoleUsers(1L, Collections.emptyList());

            // Assert
            verify(userRoleMapper, never()).batchDelete(any(), any());
        }
    }
}
