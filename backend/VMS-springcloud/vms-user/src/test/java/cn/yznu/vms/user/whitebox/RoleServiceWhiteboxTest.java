package cn.yznu.vms.user.whitebox;

import cn.yznu.vms.common.enums.StatusEnum;
import cn.yznu.vms.common.exception.BusinessException;
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
 * RoleServiceImpl 白盒测试 - 综合覆盖
 * 语句覆盖 + 判定覆盖 + 条件覆盖 + 基本路径覆盖
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleServiceWhiteboxTest {

    @Mock private RoleMapper roleMapper;
    @Mock private RolePermissionMapper rolePermissionMapper;
    @Mock private UserRoleMapper userRoleMapper;
    @Mock private StringRedisTemplate redisTemplate;

    @InjectMocks private RoleServiceImpl roleService;

    // ==================== createRole 测试 ====================

    @Test @Order(1) @DisplayName("SC01 创建成功-完整流程")
    void createRole_Success() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("TEST_ROLE");
        dto.setName("测试角色");
        dto.setPermissionIds(List.of(1L, 2L));

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any())).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(100L);
            return 1;
        });

        Long id = roleService.createRole(dto);
        assertEquals(100L, id);
        verify(rolePermissionMapper).batchInsert(eq(100L), eq(List.of(1L, 2L)));
    }

    @Test @Order(2) @DisplayName("SC02 创建失败-code重复")
    void createRole_CodeDuplicate() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("SUPER_ADMIN");
        dto.setName("新角色");

        when(roleMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> roleService.createRole(dto));
    }

    @Test @Order(3) @DisplayName("SC03 创建失败-name重复")
    void createRole_NameDuplicate() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("NEW_ROLE");
        dto.setName("超级管理员");

        when(roleMapper.selectCount(any()))
            .thenReturn(0L)  // code检查
            .thenReturn(1L); // name检查

        assertThrows(BusinessException.class, () -> roleService.createRole(dto));
    }

    @Test @Order(4) @DisplayName("SC04 创建成功-无权限")
    void createRole_NoPermissions() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_NO_PERM");
        dto.setName("无权限角色");
        dto.setPermissionIds(null);

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any())).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(101L);
            return 1;
        });

        Long id = roleService.createRole(dto);
        assertNotNull(id);
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    // ==================== deleteRole 测试 ====================

    @Test @Order(5) @DisplayName("SC05 删除成功")
    void deleteRole_Success() {
        Role role = new Role();
        role.setId(10L);
        role.setCode("NORMAL_ROLE");

        when(roleMapper.selectById(10L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(10L)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> roleService.deleteRole(10L));
        verify(roleMapper).deleteById(10L);
    }

    @Test @Order(6) @DisplayName("SC06 删除失败-不存在")
    void deleteRole_NotFound() {
        when(roleMapper.selectById(99999L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> roleService.deleteRole(99999L));
    }

    @Test @Order(7) @DisplayName("SC07 删除失败-系统角色")
    void deleteRole_SystemRole() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("SUPER_ADMIN");

        when(roleMapper.selectById(1L)).thenReturn(role);
        assertThrows(BusinessException.class, () -> roleService.deleteRole(1L));
    }

    @Test @Order(8) @DisplayName("SC08 删除失败-有用户")
    void deleteRole_HasUsers() {
        Role role = new Role();
        role.setId(10L);
        role.setCode("NORMAL_ROLE");

        when(roleMapper.selectById(10L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(10L)).thenReturn(List.of(1L, 2L));

        assertThrows(BusinessException.class, () -> roleService.deleteRole(10L));
    }

    // ==================== updateRole 测试 ====================

    @Test @Order(9) @DisplayName("SC09 更新成功")
    void updateRole_Success() {
        Role role = new Role();
        role.setId(10L);
        role.setCode("NORMAL_ROLE");
        role.setName("原名称");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("新名称");

        when(roleMapper.selectById(10L)).thenReturn(role);
        when(roleMapper.selectCount(any())).thenReturn(0L);

        assertDoesNotThrow(() -> roleService.updateRole(10L, dto));
        verify(roleMapper).updateById(any());
    }

    @Test @Order(10) @DisplayName("SC10 更新失败-不存在")
    void updateRole_NotFound() {
        when(roleMapper.selectById(99999L)).thenReturn(null);

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("新名称");

        assertThrows(BusinessException.class, () -> roleService.updateRole(99999L, dto));
    }

    @Test @Order(11) @DisplayName("DC01 判定覆盖-禁用系统角色")
    void updateRole_DisableSystemRole() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("SUPER_ADMIN");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setEnable(false);

        when(roleMapper.selectById(1L)).thenReturn(role);
        assertThrows(BusinessException.class, () -> roleService.updateRole(1L, dto));
    }

    @Test @Order(12) @DisplayName("DC02 判定覆盖-清空系统角色权限")
    void updateRole_ClearSystemPermissions() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("admin");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(Collections.emptyList());

        when(roleMapper.selectById(1L)).thenReturn(role);
        assertThrows(BusinessException.class, () -> roleService.updateRole(1L, dto));
    }

    // ==================== 条件覆盖 ====================

    @Test @Order(13) @DisplayName("CC01 条件覆盖-enable=true")
    void createRole_EnableTrue() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_ENABLED");
        dto.setName("启用角色");
        dto.setEnable(true);

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any())).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(102L);
            assertEquals(StatusEnum.ENABLED, r.getStatus());
            return 1;
        });

        roleService.createRole(dto);
    }

    @Test @Order(14) @DisplayName("CC02 条件覆盖-enable=false")
    void createRole_EnableFalse() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_DISABLED");
        dto.setName("禁用角色");
        dto.setEnable(false);

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any())).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(103L);
            assertEquals(StatusEnum.DISABLED, r.getStatus());
            return 1;
        });

        roleService.createRole(dto);
    }

    @Test @Order(15) @DisplayName("CC03 条件覆盖-sort指定值")
    void createRole_SortSpecified() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_SORT");
        dto.setName("排序角色");
        dto.setSort(100);

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any())).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(104L);
            assertEquals(100, r.getSort());
            return 1;
        });

        roleService.createRole(dto);
    }

    @Test @Order(16) @DisplayName("CC04 条件覆盖-sort为null默认0")
    void createRole_SortNull() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_SORT_NULL");
        dto.setName("默认排序角色");
        dto.setSort(null);

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any())).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(105L);
            assertEquals(0, r.getSort());
            return 1;
        });

        roleService.createRole(dto);
    }

    // ==================== 循环覆盖 ====================

    @Test @Order(17) @DisplayName("LC01 循环覆盖-权限列表空")
    void createRole_EmptyPermissions() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_EMPTY_PERM");
        dto.setName("空权限角色");
        dto.setPermissionIds(Collections.emptyList());

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any())).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(106L);
            return 1;
        });

        roleService.createRole(dto);
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    @Test @Order(18) @DisplayName("LC02 循环覆盖-权限列表1个")
    void createRole_OnePermission() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_ONE_PERM");
        dto.setName("单权限角色");
        dto.setPermissionIds(List.of(1L));

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any())).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(107L);
            return 1;
        });

        roleService.createRole(dto);
        verify(rolePermissionMapper).batchInsert(eq(107L), eq(List.of(1L)));
    }

    @Test @Order(19) @DisplayName("LC03 循环覆盖-权限列表多个")
    void createRole_MultiplePermissions() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_MULTI_PERM");
        dto.setName("多权限角色");
        dto.setPermissionIds(List.of(1L, 2L, 3L, 4L, 5L));

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any())).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(108L);
            return 1;
        });

        roleService.createRole(dto);
        verify(rolePermissionMapper).batchInsert(eq(108L), eq(List.of(1L, 2L, 3L, 4L, 5L)));
    }

    @Test @Order(20) @DisplayName("BP01 基本路径-admin角色禁止删除")
    void deleteRole_AdminRole() {
        Role role = new Role();
        role.setId(3L);
        role.setCode("admin");

        when(roleMapper.selectById(3L)).thenReturn(role);
        assertThrows(BusinessException.class, () -> roleService.deleteRole(3L));
    }
}
