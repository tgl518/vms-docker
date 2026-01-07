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
 * 角色管理 - 判定表测试 (18个用例)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleDecisionTableTest {

    static final String BASE_URL = "http://localhost:8080";
    static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
    static final ObjectMapper mapper = new ObjectMapper();
    static final List<Long> createdIds = new ArrayList<>();
    static String jwtToken;
    static Long testRoleId;

    @BeforeAll
    static void login() throws Exception {
        Map<String, String> dto = Map.of("username", "admin", "password", "123456");
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/user/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(dto)))
            .build();
        JsonNode json = mapper.readTree(client.send(req, HttpResponse.BodyHandlers.ofString()).body());
        jwtToken = json.get("data").get("accessToken").asText();
    }

    // ==================== 创建角色判定表 ====================

    @Test @Order(1) @DisplayName("DT01 创建-code有效+name有效")
    void dt01_CreateSuccess() throws Exception {
        var resp = create("DT_" + System.currentTimeMillis(), "判定表角色_" + System.currentTimeMillis());
        assertEquals(0, resp.get("code").asInt());
        testRoleId = resp.get("data").asLong();
        createdIds.add(testRoleId);
    }

    @Test @Order(2) @DisplayName("DT02 创建-code有效+name重复")
    void dt02_CreateNameDuplicate() throws Exception {
        var resp = create("DT2_" + System.currentTimeMillis(), "超级管理员");
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(3) @DisplayName("DT03 创建-code有效+name空")
    void dt03_CreateNameEmpty() throws Exception {
        var resp = create("DT3_" + System.currentTimeMillis(), "");
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(4) @DisplayName("DT04 创建-code重复+name有效")
    void dt04_CreateCodeDuplicate() throws Exception {
        var resp = create("SUPER_ADMIN", "新角色" + System.currentTimeMillis());
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(5) @DisplayName("DT05 创建-code空+name有效")
    void dt05_CreateCodeEmpty() throws Exception {
        var resp = create("", "角色" + System.currentTimeMillis());
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(6) @DisplayName("DT06 创建-code空+name空")
    void dt06_CreateBothEmpty() throws Exception {
        var resp = create("", "");
        assertNotEquals(0, resp.get("code").asInt());
    }

    // ==================== 更新角色判定表 ====================

    @Test @Order(7) @DisplayName("DT07 更新-普通角色修改名称")
    void dt07_UpdateNormalName() throws Exception {
        assertNotNull(testRoleId);
        var resp = patch("/user/role/" + testRoleId, Map.of("name", "新名称_" + System.currentTimeMillis()));
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(8) @DisplayName("DT08 更新-普通角色禁用")
    void dt08_UpdateNormalDisable() throws Exception {
        var resp = patch("/user/role/" + testRoleId, Map.of("enable", false));
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(9) @DisplayName("DT09 更新-普通角色清空权限")
    void dt09_UpdateNormalClearPerm() throws Exception {
        var resp = patch("/user/role/" + testRoleId, Map.of("permissionIds", List.of()));
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(10) @DisplayName("DT10 更新-系统角色修改名称")
    void dt10_UpdateSystemName() throws Exception {
        var resp = patch("/user/role/1", Map.of("description", "修改描述"));
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(11) @DisplayName("DT11 更新-系统角色禁用(失败)")
    void dt11_UpdateSystemDisable() throws Exception {
        var resp = patch("/user/role/1", Map.of("enable", false));
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(12) @DisplayName("DT12 更新-系统角色清空权限(失败)")
    void dt12_UpdateSystemClearPerm() throws Exception {
        var resp = patch("/user/role/1", Map.of("permissionIds", List.of()));
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(13) @DisplayName("DT13 更新-角色不存在")
    void dt13_UpdateNotExist() throws Exception {
        var resp = patch("/user/role/99999", Map.of("name", "新名称"));
        assertEquals(404, resp.get("code").asInt());
    }

    // ==================== 删除角色判定表 ====================

    @Test @Order(14) @DisplayName("DT14 删除-普通角色成功")
    void dt14_DeleteNormal() throws Exception {
        var createResp = create("DEL_DT_" + System.currentTimeMillis(), "删除测试DT_" + System.currentTimeMillis());
        Long delId = createResp.get("data").asLong();
        var resp = delete("/user/role/" + delId);
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(15) @DisplayName("DT15 删除-有用户关联(失败)")
    void dt15_DeleteWithUsers() throws Exception {
        var resp = delete("/user/role/2");
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(16) @DisplayName("DT16 删除-SUPER_ADMIN(失败)")
    void dt16_DeleteSuperAdmin() throws Exception {
        var resp = delete("/user/role/1");
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(17) @DisplayName("DT17 删除-admin(失败)")
    void dt17_DeleteAdmin() throws Exception {
        var resp = delete("/user/role/3");
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(18) @DisplayName("DT18 删除-不存在")
    void dt18_DeleteNotExist() throws Exception {
        var resp = delete("/user/role/99999");
        assertEquals(404, resp.get("code").asInt());
    }

    @AfterAll
    static void cleanup() {
        for (Long id : createdIds) {
            try { delete("/user/role/" + id); } catch (Exception ignored) {}
        }
    }

    // ==================== 工具方法 ====================
    static JsonNode create(String code, String name) throws Exception {
        return post("/user/role", Map.of("code", code, "name", name));
    }
    static JsonNode get(String path) throws Exception { return request("GET", path, null); }
    static JsonNode post(String path, Object body) throws Exception { return request("POST", path, body); }
    static JsonNode patch(String path, Object body) throws Exception { return request("PATCH", path, body); }
    static JsonNode delete(String path) throws Exception { return request("DELETE", path, null); }
    static JsonNode request(String method, String path, Object body) throws Exception {
        var builder = HttpRequest.newBuilder().uri(URI.create(BASE_URL + path))
            .header("Content-Type", "application/json").header("Authorization", "Bearer " + jwtToken);
        if (body != null) builder.method(method, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
        else if ("DELETE".equals(method)) builder.DELETE();
        else builder.GET();
        return mapper.readTree(client.send(builder.build(), HttpResponse.BodyHandlers.ofString()).body());
    }
}
