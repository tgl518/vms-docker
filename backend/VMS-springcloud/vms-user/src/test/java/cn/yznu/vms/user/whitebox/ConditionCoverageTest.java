package cn.yznu.vms.user.whitebox;

import cn.yznu.vms.common.enums.StatusEnum;
import cn.yznu.vms.user.dto.RoleCreateDTO;
import cn.yznu.vms.user.dto.RoleUpdateDTO;
import cn.yznu.vms.user.entity.Role;
import cn.yznu.vms.user.mapper.RoleMapper;
import cn.yznu.vms.user.mapper.RolePermissionMapper;
import cn.yznu.vms.user.mapper.UserRoleMapper;
import cn.yznu.vms.user.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 白盒测试 - 条件覆盖（Condition Coverage）
 * 
 * 目标：每个条件的真假值都要测试
 * 
 * 测试策略：
 * - 识别复合条件中的所有原子条件
 * - 确保每个原子条件至少有一次为真、一次为假
 * - 不要求覆盖所有条件组合
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConditionCoverageTest {

    @Mock private RoleMapper roleMapper;
    @Mock private RolePermissionMapper rolePermissionMapper;
    @Mock private UserRoleMapper userRoleMapper;
    @Mock private StringRedisTemplate redisTemplate;

    @InjectMocks private RoleServiceImpl roleService;

    // ==================== createRole 条件覆盖 ====================
    // 复合条件: permissionIds != null && !permissionIds.isEmpty()
    // 原子条件1: permissionIds != null
    // 原子条件2: !permissionIds.isEmpty()

    @Test
    @Order(1)
    @DisplayName("CC01 - 条件: permissionIds != null = TRUE")
    void testCreateRole_PermissionIdsNotNull_True() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_1");
        dto.setName("角色1");
        dto.setPermissionIds(List.of(1L)); // != null 为 TRUE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(1L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证条件1为TRUE的情况
        assertNotNull(dto.getPermissionIds());
        verify(rolePermissionMapper).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(2)
    @DisplayName("CC02 - 条件: permissionIds != null = FALSE")
    void testCreateRole_PermissionIdsNotNull_False() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_2");
        dto.setName("角色2");
        dto.setPermissionIds(null); // != null 为 FALSE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(2L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证条件1为FALSE的情况
        assertNull(dto.getPermissionIds());
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(3)
    @DisplayName("CC03 - 条件: !permissionIds.isEmpty() = TRUE")
    void testCreateRole_PermissionIdsNotEmpty_True() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_3");
        dto.setName("角色3");
        dto.setPermissionIds(List.of(1L, 2L)); // !isEmpty() 为 TRUE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(3L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证条件2为TRUE的情况
        assertFalse(dto.getPermissionIds().isEmpty());
        verify(rolePermissionMapper).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(4)
    @DisplayName("CC04 - 条件: !permissionIds.isEmpty() = FALSE")
    void testCreateRole_PermissionIdsNotEmpty_False() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_4");
        dto.setName("角色4");
        dto.setPermissionIds(Collections.emptyList()); // !isEmpty() 为 FALSE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(4L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证条件2为FALSE的情况
        assertTrue(dto.getPermissionIds().isEmpty());
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    // ==================== createRole 条件覆盖: enable 条件 ====================
    // 三元表达式: dto.getEnable() != null && dto.getEnable() ? ENABLED : DISABLED
    // 原子条件3: dto.getEnable() != null
    // 原子条件4: dto.getEnable()

    @Test
    @Order(5)
    @DisplayName("CC05 - 条件: enable != null = TRUE, enable = TRUE")
    void testCreateRole_EnableNotNull_True_EnableTrue() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_5");
        dto.setName("角色5");
        dto.setEnable(true); // != null 为 TRUE, 值为 TRUE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            Role role = inv.getArgument(0);
            role.setId(5L);
            assertEquals(StatusEnum.ENABLED, role.getStatus());
            return 1;
        });

        roleService.createRole(dto);
    }

    @Test
    @Order(6)
    @DisplayName("CC06 - 条件: enable != null = TRUE, enable = FALSE")
    void testCreateRole_EnableNotNull_True_EnableFalse() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_6");
        dto.setName("角色6");
        dto.setEnable(false); // != null 为 TRUE, 值为 FALSE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            Role role = inv.getArgument(0);
            role.setId(6L);
            assertEquals(StatusEnum.DISABLED, role.getStatus());
            return 1;
        });

        roleService.createRole(dto);
    }

    @Test
    @Order(7)
    @DisplayName("CC07 - 条件: enable != null = FALSE")
    void testCreateRole_EnableNotNull_False() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_7");
        dto.setName("角色7");
        dto.setEnable(null); // != null 为 FALSE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            Role role = inv.getArgument(0);
            role.setId(7L);
            assertEquals(StatusEnum.DISABLED, role.getStatus());
            return 1;
        });

        roleService.createRole(dto);
    }

    // ==================== createRole 条件覆盖: sort 条件 ====================
    // 原子条件5: dto.getSort() != null

    @Test
    @Order(8)
    @DisplayName("CC08 - 条件: sort != null = TRUE")
    void testCreateRole_SortNotNull_True() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_8");
        dto.setName("角色8");
        dto.setSort(100); // != null 为 TRUE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            Role role = inv.getArgument(0);
            role.setId(8L);
            assertEquals(100, role.getSort());
            return 1;
        });

        roleService.createRole(dto);
    }

    @Test
    @Order(9)
    @DisplayName("CC09 - 条件: sort != null = FALSE")
    void testCreateRole_SortNotNull_False() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_9");
        dto.setName("角色9");
        dto.setSort(null); // != null 为 FALSE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            Role role = inv.getArgument(0);
            role.setId(9L);
            assertEquals(0, role.getSort());
            return 1;
        });

        roleService.createRole(dto);
    }

    // ==================== deleteRole 条件覆盖 ====================
    // 复合条件: userIds != null && !userIds.isEmpty()
    // 原子条件6: userIds != null
    // 原子条件7: !userIds.isEmpty()

    @Test
    @Order(10)
    @DisplayName("CC10 - 条件: userIds != null = TRUE, !isEmpty() = TRUE")
    void testDeleteRole_UserIdsNotNull_True_NotEmpty_True() {
        Role role = new Role();
        role.setId(10L);
        role.setCode("ROLE_WITH_USERS");

        when(roleMapper.selectById(10L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(10L))
            .thenReturn(List.of(1L, 2L)); // != null 为 TRUE, !isEmpty() 为 TRUE

        assertThrows(Exception.class, () -> roleService.deleteRole(10L));
    }

    @Test
    @Order(11)
    @DisplayName("CC11 - 条件: userIds != null = TRUE, !isEmpty() = FALSE")
    void testDeleteRole_UserIdsNotNull_True_NotEmpty_False() {
        Role role = new Role();
        role.setId(11L);
        role.setCode("ROLE_NO_USERS");

        when(roleMapper.selectById(11L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(11L))
            .thenReturn(Collections.emptyList()); // != null 为 TRUE, !isEmpty() 为 FALSE

        assertDoesNotThrow(() -> roleService.deleteRole(11L));
        verify(roleMapper).deleteById(11L);
    }

    @Test
    @Order(12)
    @DisplayName("CC12 - 条件: userIds != null = FALSE")
    void testDeleteRole_UserIdsNotNull_False() {
        Role role = new Role();
        role.setId(12L);
        role.setCode("ROLE_NULL_USERS");

        when(roleMapper.selectById(12L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(12L))
            .thenReturn(null); // != null 为 FALSE

        assertDoesNotThrow(() -> roleService.deleteRole(12L));
        verify(roleMapper).deleteById(12L);
    }

    // ==================== updateRole 条件覆盖 ====================
    // 复合条件: StringUtils.hasText(dto.getName()) && !dto.getName().equals(role.getName())

    @Test
    @Order(13)
    @DisplayName("CC13 - 条件: hasText(name) = TRUE, !equals = TRUE")
    void testUpdateRole_HasTextTrue_NotEqualsTrue() {
        Role role = new Role();
        role.setId(13L);
        role.setCode("ROLE_13");
        role.setName("原名称");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("新名称"); // hasText = TRUE, !equals = TRUE

        when(roleMapper.selectById(13L)).thenReturn(role);
        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        roleService.updateRole(13L, dto);

        verify(roleMapper).selectCount(any()); // 检查名称冲突
    }

    @Test
    @Order(14)
    @DisplayName("CC14 - 条件: hasText(name) = TRUE, !equals = FALSE")
    void testUpdateRole_HasTextTrue_NotEqualsFalse() {
        Role role = new Role();
        role.setId(14L);
        role.setCode("ROLE_14");
        role.setName("相同名称");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("相同名称"); // hasText = TRUE, !equals = FALSE

        when(roleMapper.selectById(14L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        roleService.updateRole(14L, dto);

        verify(roleMapper, never()).selectCount(any()); // 不检查名称冲突
    }

    @Test
    @Order(15)
    @DisplayName("CC15 - 条件: hasText(name) = FALSE")
    void testUpdateRole_HasTextFalse() {
        Role role = new Role();
        role.setId(15L);
        role.setCode("ROLE_15");
        role.setName("原名称");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName(null); // hasText = FALSE
        dto.setDescription("新描述");

        when(roleMapper.selectById(15L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        roleService.updateRole(15L, dto);

        verify(roleMapper, never()).selectCount(any());
    }
}
