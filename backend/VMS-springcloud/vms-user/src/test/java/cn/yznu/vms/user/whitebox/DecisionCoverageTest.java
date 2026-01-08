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
 * 白盒测试 - 判定覆盖（Decision Coverage / Branch Coverage）
 * 
 * 目标：每个判定的真假分支都要测试
 * 
 * 测试策略：
 * - 识别代码中的所有判定点（if语句、条件表达式等）
 * - 确保每个判定至少有一次为真、一次为假
 * - 覆盖所有分支路径
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DecisionCoverageTest {

    @Mock private RoleMapper roleMapper;
    @Mock private RolePermissionMapper rolePermissionMapper;
    @Mock private UserRoleMapper userRoleMapper;
    @Mock private StringRedisTemplate redisTemplate;

    @InjectMocks private RoleServiceImpl roleService;

    // ==================== createRole 判定覆盖 ====================

    @Test
    @Order(1)
    @DisplayName("DC01 - 判定: code重复检查 = TRUE")
    void testCreateRole_CodeDuplicateTrue() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("EXISTING_CODE");
        dto.setName("测试角色");

        // 判定: selectCount(code) > 0 → TRUE
        when(roleMapper.selectCount(any())).thenReturn(1L);

        assertThrows(Exception.class, () -> roleService.createRole(dto));
        verify(roleMapper, times(1)).selectCount(any()); // 只检查了code
    }

    @Test
    @Order(2)
    @DisplayName("DC02 - 判定: code重复检查 = FALSE, name重复检查 = TRUE")
    void testCreateRole_CodeFalseNameTrue() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("NEW_CODE");
        dto.setName("已存在的名称");

        // 判定: selectCount(code) > 0 → FALSE
        // 判定: selectCount(name) > 0 → TRUE
        when(roleMapper.selectCount(any()))
            .thenReturn(0L)  // code检查通过
            .thenReturn(1L); // name检查失败

        assertThrows(Exception.class, () -> roleService.createRole(dto));
        verify(roleMapper, times(2)).selectCount(any()); // 检查了code和name
    }

    @Test
    @Order(3)
    @DisplayName("DC03 - 判定: permissionIds != null && !isEmpty() = TRUE")
    void testCreateRole_HasPermissionsTrue() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_WITH_PERM");
        dto.setName("有权限角色");
        dto.setPermissionIds(List.of(1L, 2L)); // 判定为TRUE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(1L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证TRUE分支：调用了batchInsert
        verify(rolePermissionMapper).batchInsert(eq(1L), anyList());
    }

    @Test
    @Order(4)
    @DisplayName("DC04 - 判定: permissionIds != null && !isEmpty() = FALSE (null)")
    void testCreateRole_HasPermissionsFalse_Null() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_NO_PERM");
        dto.setName("无权限角色");
        dto.setPermissionIds(null); // 判定为FALSE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(2L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证FALSE分支：未调用batchInsert
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(5)
    @DisplayName("DC05 - 判定: permissionIds != null && !isEmpty() = FALSE (empty)")
    void testCreateRole_HasPermissionsFalse_Empty() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_EMPTY_PERM");
        dto.setName("空权限角色");
        dto.setPermissionIds(Collections.emptyList()); // 判定为FALSE

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(3L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证FALSE分支：未调用batchInsert
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    // ==================== updateRole 判定覆盖 ====================

    @Test
    @Order(6)
    @DisplayName("DC06 - 判定: role == null = TRUE")
    void testUpdateRole_RoleNullTrue() {
        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("新名称");

        when(roleMapper.selectById(999L)).thenReturn(null); // 判定为TRUE

        assertThrows(Exception.class, () -> roleService.updateRole(999L, dto));
        verify(roleMapper, never()).updateById(any(Role.class));
    }

    @Test
    @Order(7)
    @DisplayName("DC07 - 判定: role == null = FALSE")
    void testUpdateRole_RoleNullFalse() {
        Role role = new Role();
        role.setId(10L);
        role.setCode("NORMAL_ROLE");
        role.setName("原名称");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setDescription("新描述");

        when(roleMapper.selectById(10L)).thenReturn(role); // 判定为FALSE
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(10L, dto));
        verify(roleMapper).updateById(any(Role.class));
    }

    @Test
    @Order(8)
    @DisplayName("DC08 - 判定: isSystemRole && enable=false = TRUE")
    void testUpdateRole_SystemRoleDisableTrue() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("SUPER_ADMIN"); // 系统角色

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setEnable(false); // 尝试禁用

        when(roleMapper.selectById(1L)).thenReturn(role);

        // 判定为TRUE：抛异常
        assertThrows(Exception.class, () -> roleService.updateRole(1L, dto));
    }

    @Test
    @Order(9)
    @DisplayName("DC09 - 判定: isSystemRole && enable=false = FALSE (非系统角色)")
    void testUpdateRole_SystemRoleDisableFalse_NotSystem() {
        Role role = new Role();
        role.setId(10L);
        role.setCode("NORMAL_ROLE"); // 非系统角色

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setEnable(false);

        when(roleMapper.selectById(10L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        // 判定为FALSE：允许禁用
        assertDoesNotThrow(() -> roleService.updateRole(10L, dto));
        verify(roleMapper).updateById(argThat((Role r) -> 
            StatusEnum.DISABLED.equals(r.getStatus())
        ));
    }

    @Test
    @Order(10)
    @DisplayName("DC10 - 判定: isSystemRole && permissionIds.isEmpty() = TRUE")
    void testUpdateRole_SystemRoleClearPermTrue() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("admin"); // 系统角色

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(Collections.emptyList()); // 清空权限

        when(roleMapper.selectById(1L)).thenReturn(role);

        // 判定为TRUE：抛异常
        assertThrows(Exception.class, () -> roleService.updateRole(1L, dto));
    }

    @Test
    @Order(11)
    @DisplayName("DC11 - 判定: hasText(name) && !name.equals() = TRUE")
    void testUpdateRole_NameChangedTrue() {
        Role role = new Role();
        role.setId(10L);
        role.setCode("NORMAL_ROLE");
        role.setName("原名称");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("新名称"); // 名称改变

        when(roleMapper.selectById(10L)).thenReturn(role);
        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(10L, dto));

        // 验证TRUE分支：检查了名称冲突
        verify(roleMapper).selectCount(any());
    }

    @Test
    @Order(12)
    @DisplayName("DC12 - 判定: hasText(name) && !name.equals() = FALSE (名称未变)")
    void testUpdateRole_NameChangedFalse() {
        Role role = new Role();
        role.setId(10L);
        role.setCode("NORMAL_ROLE");
        role.setName("相同名称");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("相同名称"); // 名称未变
        dto.setDescription("新描述");

        when(roleMapper.selectById(10L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(10L, dto));

        // 验证FALSE分支：未检查名称冲突
        verify(roleMapper, never()).selectCount(any());
    }

    // ==================== deleteRole 判定覆盖 ====================

    @Test
    @Order(13)
    @DisplayName("DC13 - 判定: role == null = TRUE (删除)")
    void testDeleteRole_RoleNullTrue() {
        when(roleMapper.selectById(999L)).thenReturn(null); // 判定为TRUE

        assertThrows(Exception.class, () -> roleService.deleteRole(999L));
        verify(roleMapper, never()).deleteById(any(Long.class));
    }

    @Test
    @Order(14)
    @DisplayName("DC14 - 判定: isSystemRole = TRUE")
    void testDeleteRole_SystemRoleTrue() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("SUPER_ADMIN"); // 判定为TRUE

        when(roleMapper.selectById(1L)).thenReturn(role);

        assertThrows(Exception.class, () -> roleService.deleteRole(1L));
        verify(roleMapper, never()).deleteById(any(Long.class));
    }

    @Test
    @Order(15)
    @DisplayName("DC15 - 判定: userIds != null && !isEmpty() = TRUE")
    void testDeleteRole_HasUsersTrue() {
        Role role = new Role();
        role.setId(20L);
        role.setCode("NORMAL_ROLE");

        when(roleMapper.selectById(20L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(20L))
            .thenReturn(List.of(1L, 2L)); // 判定为TRUE

        assertThrows(Exception.class, () -> roleService.deleteRole(20L));
        verify(roleMapper, never()).deleteById(any(Long.class));
    }

    @Test
    @Order(16)
    @DisplayName("DC16 - 判定: 所有删除前置条件 = FALSE (成功删除)")
    void testDeleteRole_AllChecksFalse() {
        Role role = new Role();
        role.setId(30L);
        role.setCode("DELETABLE_ROLE");

        when(roleMapper.selectById(30L)).thenReturn(role); // != null → FALSE
        // 非系统角色 → FALSE
        when(userRoleMapper.selectUserIdsByRoleId(30L))
            .thenReturn(Collections.emptyList()); // isEmpty → FALSE

        assertDoesNotThrow(() -> roleService.deleteRole(30L));

        // 验证所有判定都为FALSE，执行删除
        verify(rolePermissionMapper).deleteByRoleId(30L);
        verify(roleMapper).deleteById(30L);
    }
}
