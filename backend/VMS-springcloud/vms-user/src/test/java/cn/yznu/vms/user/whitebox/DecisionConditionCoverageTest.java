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
 * 白盒测试 - 判定-条件覆盖（Decision/Condition Coverage）
 * 
 * 目标：同时满足判定覆盖和条件覆盖
 * 
 * 测试策略：
 * - 确保每个判定的所有可能结果至少出现一次
 * - 确保每个条件的所有可能值至少出现一次
 * - 使用真值表方法设计测试用例
 * 
 * 真值表示例：
 * 对于复合条件 A && B：
 * | A | B | A&&B | 用例 |
 * |---|---|------|------|
 * | T | T |  T   | TC01 |
 * | T | F |  F   | TC02 |
 * | F | - |  F   | TC03 |
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DecisionConditionCoverageTest {

    @Mock private RoleMapper roleMapper;
    @Mock private RolePermissionMapper rolePermissionMapper;
    @Mock private UserRoleMapper userRoleMapper;
    @Mock private StringRedisTemplate redisTemplate;

    @InjectMocks private RoleServiceImpl roleService;

    // ==================== createRole 判定-条件覆盖 ====================
    // 复合条件: permissionIds != null && !permissionIds.isEmpty()
    // 真值表:
    // | !=null | !isEmpty | 结果 | 用例  |
    // |--------|----------|------|-------|
    // | TRUE   | TRUE     | TRUE | DCC01 |
    // | TRUE   | FALSE    | FALSE| DCC02 |
    // | FALSE  | -        | FALSE| DCC03 |

    @Test
    @Order(1)
    @DisplayName("DCC01 - permissionIds: !=null=T, !isEmpty=T => 结果=T")
    void testCreateRole_PermCondition_TT() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_DCC01");
        dto.setName("角色DCC01");
        dto.setPermissionIds(List.of(1L, 2L)); // != null: TRUE, !isEmpty: TRUE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(1L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证判定结果为TRUE：调用batchInsert
        verify(rolePermissionMapper).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(2)
    @DisplayName("DCC02 - permissionIds: !=null=T, !isEmpty=F => 结果=F")
    void testCreateRole_PermCondition_TF() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_DCC02");
        dto.setName("角色DCC02");
        dto.setPermissionIds(Collections.emptyList()); // != null: TRUE, !isEmpty: FALSE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(2L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证判定结果为FALSE：不调用batchInsert
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(3)
    @DisplayName("DCC03 - permissionIds: !=null=F => 结果=F")
    void testCreateRole_PermCondition_F() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_DCC03");
        dto.setName("角色DCC03");
        dto.setPermissionIds(null); // != null: FALSE (短路，不评估isEmpty)

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(3L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证判定结果为FALSE：不调用batchInsert
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    // ==================== createRole enable 条件判定-条件覆盖 ====================
    // 三元表达式条件: dto.getEnable() != null && dto.getEnable()
    // 真值表:
    // | !=null | enable | status    | 用例  |
    // |--------|--------|-----------|-------|
    // | TRUE   | TRUE   | ENABLED   | DCC04 |
    // | TRUE   | FALSE  | DISABLED  | DCC05 |
    // | FALSE  | -      | DISABLED  | DCC06 |

    @Test
    @Order(4)
    @DisplayName("DCC04 - enable: !=null=T, enable=T => ENABLED")
    void testCreateRole_EnableCondition_TT() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_DCC04");
        dto.setName("角色DCC04");
        dto.setEnable(true); // != null: TRUE, enable: TRUE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            Role role = inv.getArgument(0);
            role.setId(4L);
            assertEquals(StatusEnum.ENABLED, role.getStatus());
            return 1;
        });

        roleService.createRole(dto);
    }

    @Test
    @Order(5)
    @DisplayName("DCC05 - enable: !=null=T, enable=F => DISABLED")
    void testCreateRole_EnableCondition_TF() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_DCC05");
        dto.setName("角色DCC05");
        dto.setEnable(false); // != null: TRUE, enable: FALSE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            Role role = inv.getArgument(0);
            role.setId(5L);
            assertEquals(StatusEnum.DISABLED, role.getStatus());
            return 1;
        });

        roleService.createRole(dto);
    }

    @Test
    @Order(6)
    @DisplayName("DCC06 - enable: !=null=F => DISABLED")
    void testCreateRole_EnableCondition_F() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_DCC06");
        dto.setName("角色DCC06");
        dto.setEnable(null); // != null: FALSE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            Role role = inv.getArgument(0);
            role.setId(6L);
            assertEquals(StatusEnum.DISABLED, role.getStatus());
            return 1;
        });

        roleService.createRole(dto);
    }

    // ==================== updateRole 判定-条件覆盖 ====================
    // 系统角色禁用检查: isSystemRole && enable != null && !enable
    // 真值表:
    // | isSystem | !=null | !enable | 结果 | 用例  |
    // |----------|--------|---------|------|-------|
    // | TRUE     | TRUE   | TRUE    | 异常 | DCC07 |
    // | TRUE     | TRUE   | FALSE   | 允许 | DCC08 |
    // | TRUE     | FALSE  | -       | 允许 | DCC09 |
    // | FALSE    | TRUE   | TRUE    | 允许 | DCC10 |

    @Test
    @Order(7)
    @DisplayName("DCC07 - 系统角色禁用: isSystem=T, !=null=T, !enable=T => 异常")
    void testUpdateRole_SystemDisable_TTT() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("SUPER_ADMIN"); // 系统角色

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setEnable(false); // != null: TRUE, !enable: TRUE

        when(roleMapper.selectById(1L)).thenReturn(role);

        assertThrows(Exception.class, () -> roleService.updateRole(1L, dto));
    }

    @Test
    @Order(8)
    @DisplayName("DCC08 - 系统角色启用: isSystem=T, !=null=T, !enable=F => 允许")
    void testUpdateRole_SystemEnable_TTF() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("SUPER_ADMIN"); // 系统角色

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setEnable(true); // != null: TRUE, !enable: FALSE

        when(roleMapper.selectById(1L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(1L, dto));
    }

    @Test
    @Order(9)
    @DisplayName("DCC09 - 系统角色未设置enable: isSystem=T, !=null=F => 允许")
    void testUpdateRole_SystemNoEnable_TF() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("admin"); // 系统角色

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setDescription("新描述"); // enable为null

        when(roleMapper.selectById(1L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(1L, dto));
    }

    @Test
    @Order(10)
    @DisplayName("DCC10 - 普通角色禁用: isSystem=F, !=null=T, !enable=T => 允许")
    void testUpdateRole_NormalDisable_FTT() {
        Role role = new Role();
        role.setId(10L);
        role.setCode("NORMAL_ROLE"); // 非系统角色

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setEnable(false);

        when(roleMapper.selectById(10L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(10L, dto));
        verify(roleMapper).updateById(argThat((Role r) -> 
            StatusEnum.DISABLED.equals(r.getStatus())
        ));
    }

    // ==================== updateRole 权限条件判定-条件覆盖 ====================
    // 系统角色清空权限: isSystemRole && permissionIds != null && isEmpty()
    // 真值表:
    // | isSystem | !=null | isEmpty | 结果 | 用例  |
    // |----------|--------|---------|------|-------|
    // | TRUE     | TRUE   | TRUE    | 异常 | DCC11 |
    // | TRUE     | TRUE   | FALSE   | 允许 | DCC12 |
    // | TRUE     | FALSE  | -       | 允许 | DCC13 |
    // | FALSE    | TRUE   | TRUE    | 允许 | DCC14 |

    @Test
    @Order(11)
    @DisplayName("DCC11 - 清空系统角色权限: isSystem=T, !=null=T, isEmpty=T => 异常")
    void testUpdateRole_SystemClearPerm_TTT() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("admin");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(Collections.emptyList()); // != null: TRUE, isEmpty: TRUE

        when(roleMapper.selectById(1L)).thenReturn(role);

        assertThrows(Exception.class, () -> roleService.updateRole(1L, dto));
    }

    @Test
    @Order(12)
    @DisplayName("DCC12 - 系统角色设置权限: isSystem=T, !=null=T, isEmpty=F => 允许")
    void testUpdateRole_SystemSetPerm_TTF() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("SUPER_ADMIN");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(List.of(1L, 2L)); // != null: TRUE, isEmpty: FALSE

        when(roleMapper.selectById(1L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(1L, dto));
        verify(rolePermissionMapper).batchInsert(eq(1L), anyList());
    }

    @Test
    @Order(13)
    @DisplayName("DCC13 - 系统角色不修改权限: isSystem=T, !=null=F => 允许")
    void testUpdateRole_SystemNoPerm_TF() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("admin");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setDescription("新描述"); // permissionIds为null

        when(roleMapper.selectById(1L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(1L, dto));
        verify(rolePermissionMapper, never()).deleteByRoleId(anyLong());
    }

    @Test
    @Order(14)
    @DisplayName("DCC14 - 清空普通角色权限: isSystem=F, !=null=T, isEmpty=T => 允许")
    void testUpdateRole_NormalClearPerm_FTT() {
        Role role = new Role();
        role.setId(10L);
        role.setCode("NORMAL_ROLE");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(Collections.emptyList());

        when(roleMapper.selectById(10L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(10L, dto));
        verify(rolePermissionMapper).deleteByRoleId(10L);
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    // ==================== deleteRole 判定-条件覆盖 ====================
    // 用户关联检查: userIds != null && !userIds.isEmpty()
    // 真值表:
    // | !=null | !isEmpty | 结果 | 用例  |
    // |--------|----------|------|-------|
    // | TRUE   | TRUE     | 异常 | DCC15 |
    // | TRUE   | FALSE    | 允许 | DCC16 |
    // | FALSE  | -        | 允许 | DCC17 |

    @Test
    @Order(15)
    @DisplayName("DCC15 - 有用户: !=null=T, !isEmpty=T => 异常")
    void testDeleteRole_UserCondition_TT() {
        Role role = new Role();
        role.setId(20L);
        role.setCode("ROLE_WITH_USERS");

        when(roleMapper.selectById(20L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(20L))
            .thenReturn(List.of(1L, 2L)); // != null: TRUE, !isEmpty: TRUE

        assertThrows(Exception.class, () -> roleService.deleteRole(20L));
    }

    @Test
    @Order(16)
    @DisplayName("DCC16 - 无用户: !=null=T, !isEmpty=F => 允许")
    void testDeleteRole_UserCondition_TF() {
        Role role = new Role();
        role.setId(21L);
        role.setCode("ROLE_NO_USERS");

        when(roleMapper.selectById(21L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(21L))
            .thenReturn(Collections.emptyList()); // != null: TRUE, !isEmpty: FALSE

        assertDoesNotThrow(() -> roleService.deleteRole(21L));
        verify(roleMapper).deleteById(21L);
    }

    @Test
    @Order(17)
    @DisplayName("DCC17 - null用户列表: !=null=F => 允许")
    void testDeleteRole_UserCondition_F() {
        Role role = new Role();
        role.setId(22L);
        role.setCode("ROLE_NULL_USERS");

        when(roleMapper.selectById(22L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(22L))
            .thenReturn(null); // != null: FALSE

        assertDoesNotThrow(() -> roleService.deleteRole(22L));
        verify(roleMapper).deleteById(22L);
    }
}
