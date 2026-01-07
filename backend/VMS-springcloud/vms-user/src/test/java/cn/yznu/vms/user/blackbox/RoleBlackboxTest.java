package cn.yznu.vms.user.blackbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 角色管理模块 - 黑盒测试 (JUnit 5)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleBlackboxTest {

    static final String BASE_URL = "http://localhost:8080";
    static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
    static final ObjectMapper mapper = new ObjectMapper();
    static final List<Long> createdIds = new ArrayList<>();
    static Long testRoleId;
    static String jwtToken; // JWT令牌

    // ==================== 登录获取Token ====================

    @BeforeAll
    static void login() throws Exception {
        System.out.println("正在登录获取JWT Token...");
        Map<String, String> loginDto = Map.of("username", "admin", "password", "123456");
        
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/user/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(loginDto)))
            .build();
        
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode json = mapper.readTree(resp.body());
        
        assertEquals(0, json.get("code").asInt(), "登录失败: " + json.get("message").asText());
        jwtToken = json.get("data").get("accessToken").asText();
        System.out.println("登录成功，Token: " + jwtToken.substring(0, 20) + "...");
    }

    // ==================== 创建角色测试 ====================

    @Test @Order(1) @DisplayName("TC01 创建角色-完整参数")
    void testCreateWithFullParams() throws Exception {
        var resp = create("TEST_" + System.currentTimeMillis(), "测试角色_" + System.currentTimeMillis(), true);
        assertEquals(0, resp.get("code").asInt(), resp.get("message").asText());
        assertNotNull(resp.get("data"));
        testRoleId = resp.get("data").asLong();
        createdIds.add(testRoleId);
    }

    @Test @Order(2) @DisplayName("TC02 创建角色-最小参数")
    void testCreateWithMinParams() throws Exception {
        var resp = create("MIN_" + System.currentTimeMillis(), "最小角色_" + System.currentTimeMillis(), null);
        assertEquals(0, resp.get("code").asInt(), resp.get("message").asText());
        createdIds.add(resp.get("data").asLong());
    }

    @Test @Order(3) @DisplayName("TC03 创建角色-code为空")
    void testCreateCodeEmpty() throws Exception {
        var resp = create("", "角色1", null);
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(4) @DisplayName("TC04 创建角色-name为空")
    void testCreateNameEmpty() throws Exception {
        var resp = create("C_" + System.currentTimeMillis(), "", null);
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(5) @DisplayName("TC05 创建角色-code重复")
    void testCreateCodeDuplicate() throws Exception {
        var resp = create("SUPER_ADMIN", "新角色", null);
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(6) @DisplayName("TC06 创建角色-name重复")
    void testCreateNameDuplicate() throws Exception {
        var resp = create("NEW_" + System.currentTimeMillis(), "超级管理员", null);
        assertNotEquals(0, resp.get("code").asInt());
    }

    // ==================== 查询角色测试 ====================

    @Test @Order(7) @DisplayName("TC07 查询角色-存在的ID")
    void testGetExisting() throws Exception {
        var resp = get("/user/role/1");
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(8) @DisplayName("TC08 查询角色-不存在的ID")
    void testGetNotExist() throws Exception {
        var resp = get("/user/role/99999");
        assertEquals(404, resp.get("code").asInt());
    }

    @Test @Order(9) @DisplayName("TC09 分页查询-默认参数")
    void testPageDefault() throws Exception {
        var resp = get("/user/role/page");
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(10) @DisplayName("TC10 分页查询-按名称过滤")
    void testPageFilterByName() throws Exception {
        var resp = get("/user/role/page?name=管理");
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(11) @DisplayName("TC11 分页查询-按状态过滤")
    void testPageFilterByEnable() throws Exception {
        var resp = get("/user/role/page?enable=true");
        assertEquals(0, resp.get("code").asInt());
    }

    // ==================== 更新角色测试 ====================

    @Test @Order(12) @DisplayName("TC12 更新角色-修改名称")
    void testUpdateName() throws Exception {
        assertNotNull(testRoleId, "需要先执行TC01创建测试角色");
        var resp = patch("/user/role/" + testRoleId, Map.of("name", "新名称_" + System.currentTimeMillis()));
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(13) @DisplayName("TC13 更新角色-修改排序")
    void testUpdateSort() throws Exception {
        assertNotNull(testRoleId);
        var resp = patch("/user/role/" + testRoleId, Map.of("sort", 100));
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(14) @DisplayName("TC14 更新角色-不存在的ID")
    void testUpdateNotExist() throws Exception {
        var resp = patch("/user/role/99999", Map.of("name", "新名称"));
        assertEquals(404, resp.get("code").asInt());
    }

    @Test @Order(15) @DisplayName("TC15 更新角色-禁用SUPER_ADMIN")
    void testDisableSuperAdmin() throws Exception {
        var resp = patch("/user/role/1", Map.of("enable", false));
        assertNotEquals(0, resp.get("code").asInt(), "系统角色不能被禁用");
    }

    @Test @Order(16) @DisplayName("TC16 更新角色-清空SUPER_ADMIN权限")
    void testClearSuperAdminPermissions() throws Exception {
        var resp = patch("/user/role/1", Map.of("permissionIds", List.of()));
        assertNotEquals(0, resp.get("code").asInt(), "系统角色权限不能清空");
    }

    // ==================== 删除角色测试 ====================

    @Test @Order(17) @DisplayName("TC17 删除角色-不存在的ID")
    void testDeleteNotExist() throws Exception {
        var resp = delete("/user/role/99999");
        assertEquals(404, resp.get("code").asInt());
    }

    @Test @Order(18) @DisplayName("TC18 删除角色-SUPER_ADMIN")
    void testDeleteSuperAdmin() throws Exception {
        var resp = delete("/user/role/1");
        assertNotEquals(0, resp.get("code").asInt(), "系统角色不能被删除");
    }

    @Test @Order(19) @DisplayName("TC19 删除角色-有用户的角色")
    void testDeleteRoleWithUsers() throws Exception {
        var resp = delete("/user/role/2");
        assertNotEquals(0, resp.get("code").asInt(), "有用户关联的角色不能删除");
    }

    @Test @Order(20) @DisplayName("TC20 删除角色-普通角色成功")
    void testDeleteNormalRole() throws Exception {
        var createResp = create("DEL_" + System.currentTimeMillis(), "删除测试_" + System.currentTimeMillis(), null);
        assertEquals(0, createResp.get("code").asInt());
        Long delId = createResp.get("data").asLong();
        var resp = delete("/user/role/" + delId);
        assertEquals(0, resp.get("code").asInt());
    }

    // ==================== 清理 ====================

    @AfterAll
    static void cleanup() {
        for (Long id : createdIds) {
            try { delete("/user/role/" + id); } catch (Exception ignored) {}
        }
    }

    // ==================== 工具方法 ====================

    static JsonNode create(String code, String name, Boolean enable) throws Exception {
        Map<String, Object> dto = new HashMap<>();
        dto.put("code", code);
        dto.put("name", name);
        if (enable != null) dto.put("enable", enable);
        return post("/user/role", dto);
    }

    static JsonNode get(String path) throws Exception { return request("GET", path, null); }
    static JsonNode post(String path, Object body) throws Exception { return request("POST", path, body); }
    static JsonNode patch(String path, Object body) throws Exception { return request("PATCH", path, body); }
    static JsonNode delete(String path) throws Exception { return request("DELETE", path, null); }

    static JsonNode request(String method, String path, Object body) throws Exception {
        var builder = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + path))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + jwtToken); // 使用JWT Token认证
        if (body != null) builder.method(method, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
        else if ("DELETE".equals(method)) builder.DELETE();
        else builder.GET();
        return mapper.readTree(client.send(builder.build(), HttpResponse.BodyHandlers.ofString()).body());
    }
}
