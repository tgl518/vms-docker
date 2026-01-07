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
 * 角色管理 - 场景法测试 (22个用例)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleScenarioTest {

    static final String BASE_URL = "http://localhost:8080";
    static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
    static final ObjectMapper mapper = new ObjectMapper();
    static String jwtToken;
    static Long scenarioRoleId;

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

    // ==================== 基本流：完整生命周期 ====================

    @Test @Order(1) @DisplayName("SC01 基本流-创建角色")
    void sc01_Create() throws Exception {
        var resp = post("/user/role", Map.of("code", "SCENE_" + System.currentTimeMillis(), "name", "场景测试角色_" + System.currentTimeMillis()));
        assertEquals(0, resp.get("code").asInt());
        scenarioRoleId = resp.get("data").asLong();
    }

    @Test @Order(2) @DisplayName("SC02 基本流-查询角色详情")
    void sc02_GetById() throws Exception {
        assertNotNull(scenarioRoleId);
        var resp = get("/user/role/" + scenarioRoleId);
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(3) @DisplayName("SC03 基本流-修改名称")
    void sc03_UpdateName() throws Exception {
        var resp = patch("/user/role/" + scenarioRoleId, Map.of("name", "修改后的名字_" + System.currentTimeMillis()));
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(4) @DisplayName("SC04 基本流-添加描述")
    void sc04_UpdateDesc() throws Exception {
        var resp = patch("/user/role/" + scenarioRoleId, Map.of("description", "这是角色描述"));
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(5) @DisplayName("SC05 基本流-修改排序")
    void sc05_UpdateSort() throws Exception {
        var resp = patch("/user/role/" + scenarioRoleId, Map.of("sort", 50));
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(6) @DisplayName("SC06 基本流-分配权限")
    void sc06_UpdatePerm() throws Exception {
        var resp = patch("/user/role/" + scenarioRoleId, Map.of("permissionIds", List.of(1L, 2L)));
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(7) @DisplayName("SC07 基本流-分页查询验证")
    void sc07_PageQuery() throws Exception {
        var resp = get("/user/role/page?name=修改后");
        assertEquals(0, resp.get("code").asInt());
    }

    @Test @Order(8) @DisplayName("SC08 基本流-删除角色")
    void sc08_Delete() throws Exception {
        var resp = delete("/user/role/" + scenarioRoleId);
        assertEquals(0, resp.get("code").asInt());
        scenarioRoleId = null;
    }

    // ==================== 备选流1：创建失败 ====================

    @Test @Order(9) @DisplayName("SC09 备选流-创建code为空")
    void sc09_CreateCodeEmpty() throws Exception {
        var resp = post("/user/role", Map.of("code", "", "name", "角色1"));
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(10) @DisplayName("SC10 备选流-创建name为空")
    void sc10_CreateNameEmpty() throws Exception {
        var resp = post("/user/role", Map.of("code", "SC10_" + System.currentTimeMillis(), "name", ""));
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(11) @DisplayName("SC11 备选流-创建code重复")
    void sc11_CreateCodeDup() throws Exception {
        var resp = post("/user/role", Map.of("code", "SUPER_ADMIN", "name", "新角色"));
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(12) @DisplayName("SC12 备选流-创建name重复")
    void sc12_CreateNameDup() throws Exception {
        var resp = post("/user/role", Map.of("code", "SC12_" + System.currentTimeMillis(), "name", "超级管理员"));
        assertNotEquals(0, resp.get("code").asInt());
    }

    // ==================== 备选流2：修改系统角色失败 ====================

    @Test @Order(13) @DisplayName("SC13 备选流-禁用SUPER_ADMIN")
    void sc13_DisableSuperAdmin() throws Exception {
        var resp = patch("/user/role/1", Map.of("enable", false));
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(14) @DisplayName("SC14 备选流-禁用admin")
    void sc14_DisableAdmin() throws Exception {
        var resp = patch("/user/role/3", Map.of("enable", false));
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(15) @DisplayName("SC15 备选流-清空SUPER_ADMIN权限")
    void sc15_ClearSuperAdminPerm() throws Exception {
        var resp = patch("/user/role/1", Map.of("permissionIds", List.of()));
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(16) @DisplayName("SC16 备选流-清空admin权限")
    void sc16_ClearAdminPerm() throws Exception {
        var resp = patch("/user/role/3", Map.of("permissionIds", List.of()));
        assertNotEquals(0, resp.get("code").asInt());
    }

    // ==================== 备选流3：删除失败 ====================

    @Test @Order(17) @DisplayName("SC17 备选流-删除SUPER_ADMIN")
    void sc17_DeleteSuperAdmin() throws Exception {
        var resp = delete("/user/role/1");
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(18) @DisplayName("SC18 备选流-删除admin")
    void sc18_DeleteAdmin() throws Exception {
        var resp = delete("/user/role/3");
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(19) @DisplayName("SC19 备选流-删除有用户的角色")
    void sc19_DeleteRoleWithUsers() throws Exception {
        var resp = delete("/user/role/2");
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(20) @DisplayName("SC20 备选流-删除不存在")
    void sc20_DeleteNotExist() throws Exception {
        var resp = delete("/user/role/99999");
        assertEquals(404, resp.get("code").asInt());
    }

    // ==================== 备选流4：查询失败 ====================

    @Test @Order(21) @DisplayName("SC21 备选流-查询不存在")
    void sc21_GetNotExist() throws Exception {
        var resp = get("/user/role/99999");
        assertEquals(404, resp.get("code").asInt());
    }

    @Test @Order(22) @DisplayName("SC22 备选流-更新不存在")
    void sc22_UpdateNotExist() throws Exception {
        var resp = patch("/user/role/99999", Map.of("name", "xxx"));
        assertEquals(404, resp.get("code").asInt());
    }

    // ==================== 工具方法 ====================
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
