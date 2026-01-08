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
 * 白盒测试 - 基本路径覆盖（Basic Path Coverage）
 * 
 * 目标：覆盖程序的所有独立路径
 * 
 * 测试策略：
 * - 使用环路复杂度 V(G) = E - N + 2 或 V(G) = 判定节点数 + 1
 * - 识别程序的独立路径
 * - 为每条独立路径设计测试用例
 * 
 * createRole 方法分析：
 * - 判定节点：code检查、name检查、permissionIds检查 = 3个
 * - V(G) = 3 + 1 = 4 条独立路径
 * 
 * updateRole 方法分析：
 * - 判定节点：role==null、系统角色&禁用、系统角色&清权限、名称检查、各字段更新、权限更新 = 6+
 * - V(G) ≈ 7-8 条独立路径
 * 
 * deleteRole 方法分析：
 * - 判定节点：role==null、系统角色检查、用户关联检查 = 3个
 * - V(G) = 3 + 1 = 4 条独立路径
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicPathCoverageTest {

    @Mock private RoleMapper roleMapper;
    @Mock private RolePermissionMapper rolePermissionMapper;
    @Mock private UserRoleMapper userRoleMapper;
    @Mock private StringRedisTemplate redisTemplate;

    @InjectMocks private RoleServiceImpl roleService;

    // ==================== createRole 基本路径覆盖 ====================
    
    @Test
    @Order(1)
    @DisplayName("BP-C01 - 路径1: code重复 → 抛异常")
    void testCreateRole_Path1_CodeDuplicate() {
        /**
         * 路径1: 入口 → code检查(TRUE) → 抛异常 → 退出
         */
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("EXISTING_CODE");
        dto.setName("测试角色");

        when(roleMapper.selectCount(any())).thenReturn(1L); // code重复

        Exception e = assertThrows(Exception.class, () -> roleService.createRole(dto));
        assertTrue(e.getMessage().contains("编码"));
        
        verify(roleMapper, times(1)).selectCount(any());
        verify(roleMapper, never()).insert(any(Role.class));
    }

    @Test
    @Order(2)
    @DisplayName("BP-C02 - 路径2: code通过 → name重复 → 抛异常")
    void testCreateRole_Path2_NameDuplicate() {
        /**
         * 路径2: 入口 → code检查(FALSE) → name检查(TRUE) → 抛异常 → 退出
         */
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("NEW_CODE");
        dto.setName("已存在名称");

        when(roleMapper.selectCount(any()))
            .thenReturn(0L)  // code通过
            .thenReturn(1L); // name重复

        Exception e = assertThrows(Exception.class, () -> roleService.createRole(dto));
        assertTrue(e.getMessage().contains("名称"));
        
        verify(roleMapper, times(2)).selectCount(any());
        verify(roleMapper, never()).insert(any(Role.class));
    }

    @Test
    @Order(3)
    @DisplayName("BP-C03 - 路径3: 检查通过 → 创建成功 → 无权限")
    void testCreateRole_Path3_SuccessNoPermissions() {
        /**
         * 路径3: 入口 → code检查(F) → name检查(F) → 创建角色 → 
         *        权限检查(F) → 不插入权限 → 返回ID
         */
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_NO_PERM");
        dto.setName("无权限角色");
        dto.setPermissionIds(null); // 无权限

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(100L);
            return 1;
        });

        Long id = roleService.createRole(dto);

        assertEquals(100L, id);
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(4)
    @DisplayName("BP-C04 - 路径4: 检查通过 → 创建成功 → 有权限")
    void testCreateRole_Path4_SuccessWithPermissions() {
        /**
         * 路径4: 入口 → code检查(F) → name检查(F) → 创建角色 → 
         *        权限检查(T) → 插入权限 → 返回ID
         */
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_WITH_PERM");
        dto.setName("有权限角色");
        dto.setPermissionIds(List.of(1L, 2L, 3L));

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(101L);
            return 1;
        });

        Long id = roleService.createRole(dto);

        assertEquals(101L, id);
        verify(rolePermissionMapper).batchInsert(eq(101L), eq(List.of(1L, 2L, 3L)));
    }

    // ==================== deleteRole 基本路径覆盖 ====================

    @Test
    @Order(5)
    @DisplayName("BP-D01 - 路径1: 角色不存在 → 抛异常")
    void testDeleteRole_Path1_NotFound() {
        /**
         * 路径1: 入口 → 查询角色(null) → 抛异常 → 退出
         */
        when(roleMapper.selectById(999L)).thenReturn(null);

        assertThrows(Exception.class, () -> roleService.deleteRole(999L));
        
        verify(roleMapper).selectById(999L);
        verify(roleMapper, never()).deleteById(any(Long.class));
    }

    @Test
    @Order(6)
    @DisplayName("BP-D02 - 路径2: 系统角色 → 抛异常")
    void testDeleteRole_Path2_SystemRole() {
        /**
         * 路径2: 入口 → 查询角色(非null) → 系统角色检查(TRUE) → 抛异常 → 退出
         */
        Role role = new Role();
        role.setId(1L);
        role.setCode("SUPER_ADMIN");

        when(roleMapper.selectById(1L)).thenReturn(role);

        Exception e = assertThrows(Exception.class, () -> roleService.deleteRole(1L));
        assertTrue(e.getMessage().contains("系统内置角色"));
        
        verify(roleMapper, never()).deleteById(any(Long.class));
    }

    @Test
    @Order(7)
    @DisplayName("BP-D03 - 路径3: 有关联用户 → 抛异常")
    void testDeleteRole_Path3_HasUsers() {
        /**
         * 路径3: 入口 → 查询角色(非null) → 系统角色检查(F) → 
         *        用户关联检查(TRUE) → 抛异常 → 退出
         */
        Role role = new Role();
        role.setId(20L);
        role.setCode("NORMAL_ROLE");

        when(roleMapper.selectById(20L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(20L)).thenReturn(List.of(1L, 2L, 3L));

        Exception e = assertThrows(Exception.class, () -> roleService.deleteRole(20L));
        assertTrue(e.getMessage().contains("仍有"));
        
        verify(roleMapper, never()).deleteById(any(Long.class));
    }

    @Test
    @Order(8)
    @DisplayName("BP-D04 - 路径4: 所有检查通过 → 成功删除")
    void testDeleteRole_Path4_Success() {
        /**
         * 路径4: 入口 → 查询角色(非null) → 系统角色检查(F) → 
         *        用户关联检查(F) → 删除权限关联 → 清缓存 → 删除角色 → 退出
         */
        Role role = new Role();
        role.setId(30L);
        role.setCode("DELETABLE_ROLE");

        when(roleMapper.selectById(30L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(30L)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> roleService.deleteRole(30L));

        verify(rolePermissionMapper).deleteByRoleId(30L);
        verify(redisTemplate).delete("role:permission:DELETABLE_ROLE");
        verify(roleMapper).deleteById(30L);
    }

    // ==================== updateRole 基本路径覆盖 ====================

    @Test
    @Order(9)
    @DisplayName("BP-U01 - 路径1: 角色不存在 → 抛异常")
    void testUpdateRole_Path1_NotFound() {
        /**
         * 路径1: 入口 → 查询角色(null) → 抛异常 → 退出
         */
        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("新名称");

        when(roleMapper.selectById(999L)).thenReturn(null);

        assertThrows(Exception.class, () -> roleService.updateRole(999L, dto));
        verify(roleMapper, never()).updateById(any(Role.class));
    }

    @Test
    @Order(10)
    @DisplayName("BP-U02 - 路径2: 系统角色禁用 → 抛异常")
    void testUpdateRole_Path2_SystemRoleDisable() {
        /**
         * 路径2: 入口 → 查询角色(非null) → 系统角色检查(T) → 
         *        禁用检查(T) → 抛异常 → 退出
         */
        Role role = new Role();
        role.setId(1L);
        role.setCode("SUPER_ADMIN");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setEnable(false);

        when(roleMapper.selectById(1L)).thenReturn(role);

        assertThrows(Exception.class, () -> roleService.updateRole(1L, dto));
    }

    @Test
    @Order(11)
    @DisplayName("BP-U03 - 路径3: 系统角色清空权限 → 抛异常")
    void testUpdateRole_Path3_SystemRoleClearPermissions() {
        /**
         * 路径3: 入口 → 查询角色(非null) → 系统角色检查(T) → 
         *        禁用检查(F) → 清权限检查(T) → 抛异常 → 退出
         */
        Role role = new Role();
        role.setId(1L);
        role.setCode("admin");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(Collections.emptyList());

        when(roleMapper.selectById(1L)).thenReturn(role);

        assertThrows(Exception.class, () -> roleService.updateRole(1L, dto));
    }

    @Test
    @Order(12)
    @DisplayName("BP-U04 - 路径4: 名称冲突 → 抛异常")
    void testUpdateRole_Path4_NameConflict() {
        /**
         * 路径4: 入口 → 查询角色(非null) → 系统角色检查(F) → 
         *        名称修改检查(T) → 名称冲突(T) → 抛异常 → 退出
         */
        Role role = new Role();
        role.setId(10L);
        role.setCode("NORMAL_ROLE");
        role.setName("原名称");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("冲突名称");

        when(roleMapper.selectById(10L)).thenReturn(role);
        when(roleMapper.selectCount(any())).thenReturn(1L); // 名称冲突

        assertThrows(Exception.class, () -> roleService.updateRole(10L, dto));
        verify(roleMapper, never()).updateById(any(Role.class));
    }

    @Test
    @Order(13)
    @DisplayName("BP-U05 - 路径5: 仅更新基本信息 → 成功")
    void testUpdateRole_Path5_UpdateBasicInfo() {
        /**
         * 路径5: 入口 → 查询角色(非null) → 系统角色检查(F) → 
         *        名称修改检查(T) → 名称冲突(F) → 更新基本信息 → 
         *        权限检查(F) → 更新角色 → 退出
         */
        Role role = new Role();
        role.setId(11L);
        role.setCode("NORMAL_ROLE");
        role.setName("原名称");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("新名称");
        dto.setDescription("新描述");
        dto.setSort(200);

        when(roleMapper.selectById(11L)).thenReturn(role);
        when(roleMapper.selectCount(any())).thenReturn(0L); // 名称不冲突
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(11L, dto));

        verify(roleMapper).updateById(argThat((Role r) -> 
            "新名称".equals(r.getName()) && 
            "新描述".equals(r.getDescription()) &&
            200 == r.getSort()
        ));
        verify(rolePermissionMapper, never()).deleteByRoleId(any());
    }

    @Test
    @Order(14)
    @DisplayName("BP-U06 - 路径6: 更新权限（非空） → 成功")
    void testUpdateRole_Path6_UpdatePermissions() {
        /**
         * 路径6: 入口 → 查询角色(非null) → 系统角色检查(F) → 
         *        名称修改检查(F) → 更新基本信息 → 权限检查(T, 非空) → 
         *        删除旧权限 → 插入新权限 → 同步缓存 → 更新角色 → 退出
         */
        Role role = new Role();
        role.setId(12L);
        role.setCode("NORMAL_ROLE");
        role.setName("角色名");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(List.of(5L, 6L, 7L));

        when(roleMapper.selectById(12L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(12L, dto));

        verify(rolePermissionMapper).deleteByRoleId(12L);
        verify(rolePermissionMapper).batchInsert(12L, List.of(5L, 6L, 7L));
    }

    @Test
    @Order(15)
    @DisplayName("BP-U07 - 路径7: 清空权限（普通角色） → 成功")
    void testUpdateRole_Path7_ClearPermissions() {
        /**
         * 路径7: 入口 → 查询角色(非null) → 系统角色检查(F) → 
         *        名称修改检查(F) → 权限检查(T, 空) → 
         *        删除旧权限 → 不插入新权限 → 同步缓存 → 更新角色 → 退出
         */
        Role role = new Role();
        role.setId(13L);
        role.setCode("NORMAL_ROLE");
        role.setName("角色名");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(Collections.emptyList());

        when(roleMapper.selectById(13L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(13L, dto));

        verify(rolePermissionMapper).deleteByRoleId(13L);
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(16)
    @DisplayName("BP-U08 - 路径8: 系统角色更新权限 → 成功")
    void testUpdateRole_Path8_SystemRoleUpdatePermissions() {
        /**
         * 路径8: 入口 → 查询角色(非null) → 系统角色检查(T) → 
         *        禁用检查(F) → 清权限检查(F, 非空) → 更新权限 → 成功 → 退出
         */
        Role role = new Role();
        role.setId(1L);
        role.setCode("SUPER_ADMIN");
        role.setName("超级管理员");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(List.of(1L, 2L, 3L, 4L)); // 非空权限列表

        when(roleMapper.selectById(1L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        assertDoesNotThrow(() -> roleService.updateRole(1L, dto));

        verify(rolePermissionMapper).deleteByRoleId(1L);
        verify(rolePermissionMapper).batchInsert(1L, List.of(1L, 2L, 3L, 4L));
    }
}
