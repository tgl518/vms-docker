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
 * 白盒测试 - 语句覆盖（Statement Coverage）
 * 
 * 目标：确保每条语句至少执行一次
 * 
 * 测试策略：
 * - 选择能够执行所有可达语句的测试用例
 * - 覆盖成功路径和基本异常路径
 * - 确保所有赋值、方法调用、返回语句都被执行
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatementCoverageTest {

    @Mock private RoleMapper roleMapper;
    @Mock private RolePermissionMapper rolePermissionMapper;
    @Mock private UserRoleMapper userRoleMapper;
    @Mock private StringRedisTemplate redisTemplate;

    @InjectMocks private RoleServiceImpl roleService;

    // ==================== createRole 语句覆盖 ====================

    @Test
    @Order(1)
    @DisplayName("SC01 - createRole 成功路径覆盖所有语句")
    void testCreateRole_AllStatements() {
        // 准备测试数据 - 包含所有可选字段以覆盖所有赋值语句
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("TEST_ROLE");
        dto.setName("测试角色");
        dto.setDescription("测试描述");
        dto.setSort(100);
        dto.setEnable(true);
        dto.setPermissionIds(List.of(1L, 2L, 3L));

        // Mock 行为
        when(roleMapper.selectCount(any())).thenReturn(0L); // code检查通过
        when(roleMapper.insert(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setId(1L);
            
            // 验证所有字段都被正确设置
            assertEquals("TEST_ROLE", role.getCode());
            assertEquals("测试角色", role.getName());
            assertEquals("测试描述", role.getDescription());
            assertEquals(100, role.getSort());
            assertEquals(StatusEnum.ENABLED, role.getStatus());
            
            return 1;
        });

        // 执行测试
        Long roleId = roleService.createRole(dto);

        // 验证结果
        assertNotNull(roleId);
        assertEquals(1L, roleId);
        
        // 验证所有语句都被执行
        verify(roleMapper, times(2)).selectCount(any()); // code + name 检查
        verify(roleMapper).insert(any(Role.class));
        verify(rolePermissionMapper).batchInsert(eq(1L), eq(List.of(1L, 2L, 3L)));
    }

    @Test
    @Order(2)
    @DisplayName("SC02 - createRole 异常路径语句覆盖")
    void testCreateRole_ExceptionPath() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("DUPLICATE_CODE");
        dto.setName("测试角色");

        // Mock 代码重复的情况
        when(roleMapper.selectCount(any())).thenReturn(1L);

        // 验证异常抛出语句被执行
        Exception exception = assertThrows(Exception.class, () -> {
            roleService.createRole(dto);
        });
        
        assertTrue(exception.getMessage().contains("已存在"));
    }

    // ==================== updateRole 语句覆盖 ====================

    @Test
    @Order(3)
    @DisplayName("SC03 - updateRole 成功路径覆盖所有语句")
    void testUpdateRole_AllStatements() {
        // 准备已存在的角色
        Role existingRole = new Role();
        existingRole.setId(10L);
        existingRole.setCode("NORMAL_ROLE");
        existingRole.setName("原名称");
        existingRole.setDescription("原描述");
        existingRole.setSort(50);
        existingRole.setStatus(StatusEnum.ENABLED);

        // 准备更新数据 - 包含所有可选字段
        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("新名称");
        dto.setDescription("新描述");
        dto.setSort(200);
        dto.setEnable(false);
        dto.setPermissionIds(List.of(5L, 6L));

        // Mock 行为
        when(roleMapper.selectById(10L)).thenReturn(existingRole);
        when(roleMapper.selectCount(any())).thenReturn(0L); // 名称不冲突
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> roleService.updateRole(10L, dto));

        // 验证所有更新语句都被执行
        verify(roleMapper).selectById(10L);
        verify(roleMapper).selectCount(any());
        verify(roleMapper).updateById(argThat((Role role) -> 
            "新名称".equals(role.getName()) &&
            "新描述".equals(role.getDescription()) &&
            200 == role.getSort() &&
            StatusEnum.DISABLED.equals(role.getStatus())
        ));
        verify(rolePermissionMapper).deleteByRoleId(10L);
        verify(rolePermissionMapper).batchInsert(10L, List.of(5L, 6L));
    }

    @Test
    @Order(4)
    @DisplayName("SC04 - updateRole 角色不存在异常路径")
    void testUpdateRole_NotFound() {
        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setName("新名称");

        when(roleMapper.selectById(999L)).thenReturn(null);

        // 验证异常抛出语句被执行
        assertThrows(Exception.class, () -> roleService.updateRole(999L, dto));
        
        verify(roleMapper).selectById(999L);
        verify(roleMapper, never()).updateById(any(Role.class));
    }

    // ==================== deleteRole 语句覆盖 ====================

    @Test
    @Order(5)
    @DisplayName("SC05 - deleteRole 成功路径覆盖所有语句")
    void testDeleteRole_AllStatements() {
        // 准备角色数据
        Role role = new Role();
        role.setId(20L);
        role.setCode("DELETABLE_ROLE");
        role.setName("可删除角色");

        // Mock 行为
        when(roleMapper.selectById(20L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(20L)).thenReturn(Collections.emptyList());
        when(roleMapper.deleteById(20L)).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> roleService.deleteRole(20L));

        // 验证所有删除相关语句都被执行
        verify(roleMapper).selectById(20L);
        verify(userRoleMapper).selectUserIdsByRoleId(20L);
        verify(rolePermissionMapper).deleteByRoleId(20L);
        verify(redisTemplate).delete("role:permission:" + role.getCode());
        verify(roleMapper).deleteById(20L);
    }

    @Test
    @Order(6)
    @DisplayName("SC06 - deleteRole 系统角色异常路径")
    void testDeleteRole_SystemRole() {
        Role role = new Role();
        role.setId(1L);
        role.setCode("SUPER_ADMIN");

        when(roleMapper.selectById(1L)).thenReturn(role);

        // 验证系统角色检查和异常抛出语句被执行
        Exception exception = assertThrows(Exception.class, () -> {
            roleService.deleteRole(1L);
        });
        
        assertTrue(exception.getMessage().contains("系统内置角色"));
        verify(roleMapper, never()).deleteById(any(Long.class));
    }

    @Test
    @Order(7)
    @DisplayName("SC07 - deleteRole 有关联用户异常路径")
    void testDeleteRole_HasUsers() {
        Role role = new Role();
        role.setId(30L);
        role.setCode("NORMAL_ROLE");

        when(roleMapper.selectById(30L)).thenReturn(role);
        when(userRoleMapper.selectUserIdsByRoleId(30L)).thenReturn(List.of(1L, 2L, 3L));

        // 验证用户关联检查和异常抛出语句被执行
        Exception exception = assertThrows(Exception.class, () -> {
            roleService.deleteRole(30L);
        });
        
        assertTrue(exception.getMessage().contains("仍有"));
        verify(roleMapper, never()).deleteById(any(Long.class));
    }
}
