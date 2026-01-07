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
 * 角色管理 - 状态迁移测试 (9个用例)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleStateTransitionTest {

    static final String BASE_URL = "http://localhost:8080";
    static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
    static final ObjectMapper mapper = new ObjectMapper();
    static String jwtToken;
    static Long enabledRoleId;  // S1状态角色
    static Long disabledRoleId; // S2状态角色

    @BeforeAll
    static void login() throws Exception {
        var req = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/user/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(Map.of("username", "admin", "password", "123456"))))
            .build();
        jwtToken = mapper.readTree(client.send(req, HttpResponse.BodyHandlers.ofString()).body()).get("data").get("accessToken").asText();
    }

    // S0 -> S1: 创建启用状态角色
    @Test @Order(1) @DisplayName("ST01 S0->S1 创建启用角色")
    void st01_CreateEnabled() throws Exception {
        var resp = post("/user/role", Map.of("code", "ST1_" + System.currentTimeMillis(), "name", "状态测试启用_" + System.currentTimeMillis(), "enable", true));
        assertEquals(0, resp.get("code").asInt());
        enabledRoleId = resp.get("data").asLong();
    }

    // S0 -> S2: 创建禁用状态角色
    @Test @Order(2) @DisplayName("ST02 S0->S2 创建禁用角色")
    void st02_CreateDisabled() throws Exception {
        var resp = post("/user/role", Map.of("code", "ST2_" + System.currentTimeMillis(), "name", "状态测试禁用_" + System.currentTimeMillis(), "enable", false));
        assertEquals(0, resp.get("code").asInt());
        disabledRoleId = resp.get("data").asLong();
    }

    // S1 -> S2: 禁用普通角色
    @Test @Order(3) @DisplayName("ST03 S1->S2 禁用普通角色")
    void st03_DisableNormal() throws Exception {
        assertNotNull(enabledRoleId);
        var resp = patch("/user/role/" + enabledRoleId, Map.of("enable", false));
        assertEquals(0, resp.get("code").asInt());
    }

    // S1 -> S1: 禁用系统角色(失败)
    @Test @Order(4) @DisplayName("ST04 S1->S1 禁用SUPER_ADMIN(失败)")
    void st04_DisableSuperAdmin() throws Exception {
        var resp = patch("/user/role/1", Map.of("enable", false));
        assertNotEquals(0, resp.get("code").asInt());
    }

    // S2 -> S1: 启用角色
    @Test @Order(5) @DisplayName("ST05 S2->S1 启用角色")
    void st05_Enable() throws Exception {
        assertNotNull(enabledRoleId);
        var resp = patch("/user/role/" + enabledRoleId, Map.of("enable", true));
        assertEquals(0, resp.get("code").asInt());
    }

    // S1 -> S3: 删除普通角色
    @Test @Order(6) @DisplayName("ST06 S1->S3 删除普通角色")
    void st06_DeleteNormal() throws Exception {
        assertNotNull(enabledRoleId);
        var resp = delete("/user/role/" + enabledRoleId);
        assertEquals(0, resp.get("code").asInt());
        enabledRoleId = null;
    }

    // S1 -> S1: 删除系统角色(失败)
    @Test @Order(7) @DisplayName("ST07 S1->S1 删除系统角色(失败)")
    void st07_DeleteSystem() throws Exception {
        var resp = delete("/user/role/1");
        assertNotEquals(0, resp.get("code").asInt());
    }

    // S2 -> S3: 删除禁用角色
    @Test @Order(8) @DisplayName("ST08 S2->S3 删除禁用角色")
    void st08_DeleteDisabled() throws Exception {
        assertNotNull(disabledRoleId);
        var resp = delete("/user/role/" + disabledRoleId);
        assertEquals(0, resp.get("code").asInt());
        disabledRoleId = null;
    }

    // S1 -> S1: 删除有用户的角色(失败)
    @Test @Order(9) @DisplayName("ST09 S1->S1 删除有用户角色(失败)")
    void st09_DeleteWithUsers() throws Exception {
        var resp = delete("/user/role/2");
        assertNotEquals(0, resp.get("code").asInt());
    }

    static JsonNode post(String p, Object b) throws Exception { return req("POST", p, b); }
    static JsonNode patch(String p, Object b) throws Exception { return req("PATCH", p, b); }
    static JsonNode delete(String p) throws Exception { return req("DELETE", p, null); }
    static JsonNode req(String m, String p, Object b) throws Exception {
        var bd = HttpRequest.newBuilder().uri(URI.create(BASE_URL + p)).header("Content-Type", "application/json").header("Authorization", "Bearer " + jwtToken);
        if (b != null) bd.method(m, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(b)));
        else if ("DELETE".equals(m)) bd.DELETE(); else bd.GET();
        return mapper.readTree(client.send(bd.build(), HttpResponse.BodyHandlers.ofString()).body());
    }
}
