package src.cn.yznu.vms.user.blackbox;

import java.util.*;

/**
 * 角色管理模块 - 黑盒测试
 * 运行: java -jar target/vms-blackbox-test-1.0.0.jar
 */
public class RoleManagementBlackboxTest extends cn.yznu.vms.user.blackbox.ApiTestBase {

    private final List<Long> createdIds = new ArrayList<>();

    public static void main(String[] args) {
        RoleManagementBlackboxTest test = new RoleManagementBlackboxTest();
        try {
            test.runAll();
        } finally {
            test.cleanup();
        }
    }

    public void runAll() {
        System.out.println("========== 角色管理黑盒测试 ==========\n");
        
        testCreate();
        testQuery();
        testUpdate();
        testDelete();
        
        System.out.println("\n========== 测试完成 ==========");
    }

    // ===== 创建角色测试 =====
    void testCreate() {
        System.out.println("【创建角色】");
        try {
            // TC01: 创建成功
            Map<String, Object> dto = new HashMap<>();
            dto.put("code", "TEST_" + System.currentTimeMillis());
            dto.put("name", "测试角色_" + System.currentTimeMillis());
            var resp = post("/user/role", dto);
            assertOk(resp, "TC01 创建成功");
            if (resp.get("data") != null) createdIds.add(resp.get("data").asLong());

            // TC03: code为空
            dto.put("code", "");
            dto.put("name", "角色" + System.currentTimeMillis());
            assertFail(post("/user/role", dto), "TC03 code为空");

            // TC04: name为空
            dto.put("code", "CODE_" + System.currentTimeMillis());
            dto.put("name", "");
            assertFail(post("/user/role", dto), "TC04 name为空");

            // TC05: code重复
            dto.put("code", "SUPER_ADMIN");
            dto.put("name", "新角色" + System.currentTimeMillis());
            assertFail(post("/user/role", dto), "TC05 code重复");

            // TC06: name重复
            dto.put("code", "NEW_" + System.currentTimeMillis());
            dto.put("name", "超级管理员");
            assertFail(post("/user/role", dto), "TC06 name重复");
            
        } catch (Exception e) {
            System.out.println("❌ 创建测试异常: " + e.getMessage());
        }
    }

    // ===== 查询角色测试 =====
    void testQuery() {
        System.out.println("\n【查询角色】");
        try {
            // TC17: 查询成功
            assertOk(get("/user/role/1"), "TC17 查询存在的角色");

            // TC18: 查询不存在
            var resp = get("/user/role/99999");
            if (resp.get("code").asInt() == 404) {
                System.out.println("✅ TC18 查询不存在返回404");
            } else {
                System.out.println("❌ TC18 应返回404");
            }

            // TC19: 分页查询
            assertOk(get("/user/role/page"), "TC19 分页查询");
            
        } catch (Exception e) {
            System.out.println("❌ 查询测试异常: " + e.getMessage());
        }
    }

    // ===== 更新角色测试 =====
    void testUpdate() {
        System.out.println("\n【更新角色】");
        try {
            // 先创建一个测试角色
            Map<String, Object> dto = new HashMap<>();
            dto.put("code", "UPD_" + System.currentTimeMillis());
            dto.put("name", "更新测试_" + System.currentTimeMillis());
            var createResp = post("/user/role", dto);
            Long testId = createResp.get("data") != null ? createResp.get("data").asLong() : null;
            if (testId != null) createdIds.add(testId);

            // TC07: 更新成功
            if (testId != null) {
                Map<String, Object> upd = new HashMap<>();
                upd.put("name", "新名称_" + System.currentTimeMillis());
                assertOk(patch("/user/role/" + testId, upd), "TC07 更新成功");
            }

            // TC08: 更新不存在的ID
            Map<String, Object> upd = new HashMap<>();
            upd.put("name", "新名称");
            var resp = patch("/user/role/99999", upd);
            if (resp.get("code").asInt() == 404) {
                System.out.println("✅ TC08 更新不存在返回404");
            }

            // TC09: 禁用SUPER_ADMIN
            upd.clear();
            upd.put("enable", false);
            assertFail(patch("/user/role/1", upd), "TC09 禁用SUPER_ADMIN");

            // TC10: 禁用admin
            assertFail(patch("/user/role/3", upd), "TC10 禁用admin");

            // TC11: 清空系统角色权限
            upd.clear();
            upd.put("permissionIds", Collections.emptyList());
            assertFail(patch("/user/role/1", upd), "TC11 清空SUPER_ADMIN权限");
            
        } catch (Exception e) {
            System.out.println("❌ 更新测试异常: " + e.getMessage());
        }
    }

    // ===== 删除角色测试 =====
    void testDelete() {
        System.out.println("\n【删除角色】");
        try {
            // TC13: 删除不存在
            var resp = delete("/user/role/99999");
            if (resp.get("code").asInt() == 404) {
                System.out.println("✅ TC13 删除不存在返回404");
            }

            // TC14: 删除SUPER_ADMIN
            assertFail(delete("/user/role/1"), "TC14 删除SUPER_ADMIN");

            // TC15: 删除admin
            assertFail(delete("/user/role/3"), "TC15 删除admin");

            // TC16: 删除有用户的角色
            assertFail(delete("/user/role/2"), "TC16 删除有用户的角色");

            // TC12: 删除普通角色成功
            Map<String, Object> dto = new HashMap<>();
            dto.put("code", "DEL_" + System.currentTimeMillis());
            dto.put("name", "删除测试_" + System.currentTimeMillis());
            var createResp = post("/user/role", dto);
            if (createResp.get("code").asInt() == 0) {
                Long delId = createResp.get("data").asLong();
                assertOk(delete("/user/role/" + delId), "TC12 删除普通角色成功");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 删除测试异常: " + e.getMessage());
        }
    }

    // ===== 清理测试数据 =====
    void cleanup() {
        for (Long id : createdIds) {
            try { delete("/user/role/" + id); } catch (Exception ignored) {}
        }
    }
}
