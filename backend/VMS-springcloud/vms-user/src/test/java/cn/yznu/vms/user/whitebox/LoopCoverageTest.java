package cn.yznu.vms.user.whitebox;

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
 * 白盒测试 - 循环覆盖（Loop Coverage）
 * 
 * 目标：测试循环的零次、一次、多次执行
 * 
 * 测试策略：
 * - 零次循环：循环体不执行
 * - 一次循环：循环体执行一次
 * - 多次循环：循环体执行多次（典型值：2次、n次）
 * - 边界值：最大值、最小值
 * 
 * 注意：RoleServiceImpl中虽然没有显式的for/while循环，
 * 但集合操作（如batchInsert(permissionIds)）内部包含隐式循环。
 * 我们针对这些集合操作进行循环覆盖测试。
 * 
 * 隐式循环点：
 * 1. permissionIds 的批量插入
 * 2. userIds 的用户关联检查
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoopCoverageTest {

    @Mock private RoleMapper roleMapper;
    @Mock private RolePermissionMapper rolePermissionMapper;
    @Mock private UserRoleMapper userRoleMapper;
    @Mock private StringRedisTemplate redisTemplate;

    @InjectMocks private RoleServiceImpl roleService;

    // ==================== createRole 权限列表循环覆盖 ====================

    @Test
    @Order(1)
    @DisplayName("LC-C01 - 权限循环: 0次（null）")
    void testCreateRole_PermissionLoop_Zero_Null() {
        /**
         * 循环次数：0次
         * permissionIds = null，不进入循环处理逻辑
         */
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_LOOP_01");
        dto.setName("循环测试01");
        dto.setPermissionIds(null); // 0次：null

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(1L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证：batchInsert未被调用（0次循环）
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(2)
    @DisplayName("LC-C02 - 权限循环: 0次（空列表）")
    void testCreateRole_PermissionLoop_Zero_Empty() {
        /**
         * 循环次数：0次
         * permissionIds = []，空列表，不进入循环处理逻辑
         */
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_LOOP_02");
        dto.setName("循环测试02");
        dto.setPermissionIds(Collections.emptyList()); // 0次：空列表

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(2L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证：batchInsert未被调用（0次循环）
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(3)
    @DisplayName("LC-C03 - 权限循环: 1次")
    void testCreateRole_PermissionLoop_One() {
        /**
         * 循环次数：1次
         * permissionIds = [1]，单个权限，循环执行1次
         */
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_LOOP_03");
        dto.setName("循环测试03");
        dto.setPermissionIds(List.of(1L)); // 1次：单个元素

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(3L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证：batchInsert被调用，处理1个权限
        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
        verify(rolePermissionMapper).batchInsert(eq(3L), captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals(1L, captor.getValue().get(0));
    }

    @Test
    @Order(4)
    @DisplayName("LC-C04 - 权限循环: 2次")
    void testCreateRole_PermissionLoop_Two() {
        /**
         * 循环次数：2次
         * permissionIds = [1, 2]，两个权限，循环执行2次
         */
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_LOOP_04");
        dto.setName("循环测试04");
        dto.setPermissionIds(List.of(1L, 2L)); // 2次：两个元素

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(4L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证：batchInsert被调用，处理2个权限
        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
        verify(rolePermissionMapper).batchInsert(eq(4L), captor.capture());
        assertEquals(2, captor.getValue().size());
        assertTrue(captor.getValue().containsAll(List.of(1L, 2L)));
    }

    @Test
    @Order(5)
    @DisplayName("LC-C05 - 权限循环: 多次（5次）")
    void testCreateRole_PermissionLoop_Multiple() {
        /**
         * 循环次数：5次
         * permissionIds = [1, 2, 3, 4, 5]，多个权限，循环执行5次
         */
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_LOOP_05");
        dto.setName("循环测试05");
        dto.setPermissionIds(List.of(1L, 2L, 3L, 4L, 5L)); // 5次：多个元素

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(5L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证：batchInsert被调用，处理5个权限
        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
        verify(rolePermissionMapper).batchInsert(eq(5L), captor.capture());
        assertEquals(5, captor.getValue().size());
        assertTrue(captor.getValue().containsAll(List.of(1L, 2L, 3L, 4L, 5L)));
    }

    @Test
    @Order(6)
    @DisplayName("LC-C06 - 权限循环: 大量数据（10次）")
    void testCreateRole_PermissionLoop_Large() {
        /**
         * 循环次数：10次
         * 测试较大数据集的循环处理
         */
        List<Long> permissions = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            permissions.add(i);
        }

        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("ROLE_LOOP_06");
        dto.setName("循环测试06");
        dto.setPermissionIds(permissions); // 10次

        when(roleMapper.selectCount(any())).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenAnswer(inv -> {
            ((Role) inv.getArgument(0)).setId(6L);
            return 1;
        });

        roleService.createRole(dto);

        // 验证：batchInsert被调用，处理10个权限
        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
        verify(rolePermissionMapper).batchInsert(eq(6L), captor.capture());
        assertEquals(10, captor.getValue().size());
    }

    // ==================== updateRole 权限列表循环覆盖 ====================

    @Test
    @Order(7)
    @DisplayName("LC-U01 - 更新权限循环: 0次（不更新）")
    void testUpdateRole_PermissionLoop_Zero_NoUpdate() {
        /**
         * 循环次数：0次
         * permissionIds = null，不更新权限
         */
        Role role = new Role();
        role.setId(10L);
        role.setCode("NORMAL_ROLE");
        role.setName("原名称");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setDescription("新描述");
        dto.setPermissionIds(null); // 不更新权限

        when(roleMapper.selectById(10L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        roleService.updateRole(10L, dto);

        // 验证：权限相关操作未执行
        verify(rolePermissionMapper, never()).deleteByRoleId(any());
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(8)
    @DisplayName("LC-U02 - 更新权限循环: 0次（清空）")
    void testUpdateRole_PermissionLoop_Zero_Clear() {
        /**
         * 循环次数：0次
         * permissionIds = []，清空权限，删除但不插入
         */
        Role role = new Role();
        role.setId(11L);
        role.setCode("NORMAL_ROLE");
        role.setName("角色名");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(Collections.emptyList()); // 清空权限

        when(roleMapper.selectById(11L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        roleService.updateRole(11L, dto);

        // 验证：删除了旧权限，但未插入新权限（0次循环）
        verify(rolePermissionMapper).deleteByRoleId(11L);
        verify(rolePermissionMapper, never()).batchInsert(anyLong(), anyList());
    }

    @Test
    @Order(9)
    @DisplayName("LC-U03 - 更新权限循环: 1次")
    void testUpdateRole_PermissionLoop_One() {
        /**
         * 循环次数：1次
         * permissionIds = [10]，更新为单个权限
         */
        Role role = new Role();
        role.setId(12L);
        role.setCode("NORMAL_ROLE");
        role.setName("角色名");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(List.of(10L)); // 1个权限

        when(roleMapper.selectById(12L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        roleService.updateRole(12L, dto);

        // 验证：处理1个权限
        verify(rolePermissionMapper).deleteByRoleId(12L);
        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
        verify(rolePermissionMapper).batchInsert(eq(12L), captor.capture());
        assertEquals(1, captor.getValue().size());
    }

    @Test
    @Order(10)
    @DisplayName("LC-U04 - 更新权限循环: 多次（3次）")
    void testUpdateRole_PermissionLoop_Multiple() {
        /**
         * 循环次数：3次
         * permissionIds = [10, 20, 30]，更新为3个权限
         */
        Role role = new Role();
        role.setId(13L);
        role.setCode("NORMAL_ROLE");
        role.setName("角色名");

        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setPermissionIds(List.of(10L, 20L, 30L)); // 3个权限

        when(roleMapper.selectById(13L)).thenReturn(role);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        roleService.updateRole(13L, dto);

        // 验证：处理3个权限
        verify(rolePermissionMapper).deleteByRoleId(13L);
        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
        verify(rolePermissionMapper).batchInsert(eq(13L), captor.capture());
        assertEquals(3, captor.getValue().size());
        assertTrue(captor.getValue().containsAll(List.of(10L, 20L, 30L)));
    }

    // ==================== deleteRole 用户列表循环覆盖 ====================

    @Test
    @Order(11)
    @DisplayName("LC-D01 - 用户列表循环: 0次（null）")
    void testDeleteRole_UserLoop_Zero_Null() {
        /**
         * 循环次数：0次
         * userIds = null，没有关联用户
         */
        Role role = new Role();
        role.setId(20L);
        role.setCode("DELETABLE_ROLE");

        when(roleMapper.selectById(20L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(20L)).thenReturn(null); // 0次

        assertDoesNotThrow(() -> roleService.deleteRole(20L));
        verify(roleMapper).deleteById(20L);
    }

    @Test
    @Order(12)
    @DisplayName("LC-D02 - 用户列表循环: 0次（空列表）")
    void testDeleteRole_UserLoop_Zero_Empty() {
        /**
         * 循环次数：0次
         * userIds = []，空用户列表
         */
        Role role = new Role();
        role.setId(21L);
        role.setCode("DELETABLE_ROLE");

        when(roleMapper.selectById(21L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(21L))
            .thenReturn(Collections.emptyList()); // 0次

        assertDoesNotThrow(() -> roleService.deleteRole(21L));
        verify(roleMapper).deleteById(21L);
    }

    @Test
    @Order(13)
    @DisplayName("LC-D03 - 用户列表循环: 1次")
    void testDeleteRole_UserLoop_One() {
        /**
         * 循环次数：1次
         * userIds = [100]，1个关联用户，阻止删除
         */
        Role role = new Role();
        role.setId(22L);
        role.setCode("ROLE_WITH_USER");

        when(roleMapper.selectById(22L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(22L))
            .thenReturn(List.of(100L)); // 1次

        Exception e = assertThrows(Exception.class, () -> roleService.deleteRole(22L));
        assertTrue(e.getMessage().contains("1"));
        verify(roleMapper, never()).deleteById(any(Long.class));
    }

    @Test
    @Order(14)
    @DisplayName("LC-D04 - 用户列表循环: 多次（3次）")
    void testDeleteRole_UserLoop_Multiple() {
        /**
         * 循环次数：3次
         * userIds = [100, 101, 102]，3个关联用户，阻止删除
         */
        Role role = new Role();
        role.setId(23L);
        role.setCode("ROLE_WITH_USERS");

        when(roleMapper.selectById(23L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(23L))
            .thenReturn(List.of(100L, 101L, 102L)); // 3次

        Exception e = assertThrows(Exception.class, () -> roleService.deleteRole(23L));
        assertTrue(e.getMessage().contains("3"));
        verify(roleMapper, never()).deleteById(any(Long.class));
    }

    @Test
    @Order(15)  
    @DisplayName("LC-D05 - 用户列表循环: 大量数据（10次）")
    void testDeleteRole_UserLoop_Large() {
        /**
         * 循环次数：10次
         * 测试大量关联用户的情况
         */
        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            userIds.add(i);
        }

        Role role = new Role();
        role.setId(24L);
        role.setCode("ROLE_WITH_MANY_USERS");

        when(roleMapper.selectById(24L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(24L)).thenReturn(userIds); // 10次

        Exception e = assertThrows(Exception.class, () -> roleService.deleteRole(24L));
        assertTrue(e.getMessage().contains("10"));
        verify(roleMapper, never()).deleteById(any(Long.class));
    }
}
