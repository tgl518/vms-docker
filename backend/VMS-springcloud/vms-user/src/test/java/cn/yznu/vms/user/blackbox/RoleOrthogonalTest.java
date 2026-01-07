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
 * 角色管理 - 正交表测试 (4个用例)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleOrthogonalTest {

    static final String BASE_URL = "http://localhost:8080";
    static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
    static final ObjectMapper mapper = new ObjectMapper();
    static String jwtToken;
    static final List<Long> createdIds = new ArrayList<>();

    @BeforeAll
    static void login() throws Exception {
        var req = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/user/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(Map.of("username", "admin", "password", "123456"))))
            .build();
        jwtToken = mapper.readTree(client.send(req, HttpResponse.BodyHandlers.ofString()).body()).get("data").get("accessToken").asText();
    }

    // 正交表 L4(2³): A=code, B=name, C=enable

    @Test @Order(1) @DisplayName("OT01 code有效+name有效+enable=true")
    void ot01() throws Exception {
        var resp = post("/user/role", Map.of("code", "OT1_" + System.currentTimeMillis(), "name", "正交1", "enable", true));
        assertEquals(0, resp.get("code").asInt());
        createdIds.add(resp.get("data").asLong());
    }

    @Test @Order(2) @DisplayName("OT02 code有效+name无效+enable=false")
    void ot02() throws Exception {
        var resp = post("/user/role", Map.of("code", "OT2_" + System.currentTimeMillis(), "name", "", "enable", false));
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(3) @DisplayName("OT03 code无效+name有效+enable=true")
    void ot03() throws Exception {
        var resp = post("/user/role", Map.of("code", "", "name", "正交3", "enable", true));
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(4) @DisplayName("OT04 code无效+name无效+enable=false")
    void ot04() throws Exception {
        var resp = post("/user/role", Map.of("code", "", "name", "", "enable", false));
        assertNotEquals(0, resp.get("code").asInt());
    }

    @AfterAll
    static void cleanup() {
        for (Long id : createdIds) { try { delete("/user/role/" + id); } catch (Exception ignored) {} }
    }

    static JsonNode post(String p, Object b) throws Exception { return req("POST", p, b); }
    static JsonNode delete(String p) throws Exception { return req("DELETE", p, null); }
    static JsonNode req(String m, String p, Object b) throws Exception {
        var bd = HttpRequest.newBuilder().uri(URI.create(BASE_URL + p)).header("Content-Type", "application/json").header("Authorization", "Bearer " + jwtToken);
        if (b != null) bd.method(m, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(b)));
        else if ("DELETE".equals(m)) bd.DELETE(); else bd.GET();
        return mapper.readTree(client.send(bd.build(), HttpResponse.BodyHandlers.ofString()).body());
    }
}
