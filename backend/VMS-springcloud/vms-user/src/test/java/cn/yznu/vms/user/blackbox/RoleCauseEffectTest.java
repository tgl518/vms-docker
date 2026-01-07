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
 * 角色管理 - 因果图测试 (5个用例)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleCauseEffectTest {

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

    // 因果图: C1(code非空) AND C2(code唯一) AND C3(name非空) AND C4(name唯一) -> E1(成功)

    @Test @Order(1) @DisplayName("CG01 C1+C2+C3+C4=T -> 成功")
    void cg01_AllTrue() throws Exception {
        var resp = create("CG1_" + System.currentTimeMillis(), "因果测试1_" + System.currentTimeMillis());
        assertEquals(0, resp.get("code").asInt());
        createdIds.add(resp.get("data").asLong());
    }

    @Test @Order(2) @DisplayName("CG02 C1=F -> 失败(code空)")
    void cg02_CodeEmpty() throws Exception {
        var resp = create("", "因果测试2_" + System.currentTimeMillis());
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(3) @DisplayName("CG03 C2=F -> 失败(code重复)")
    void cg03_CodeDup() throws Exception {
        var resp = create("SUPER_ADMIN", "因果测试3_" + System.currentTimeMillis());
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(4) @DisplayName("CG04 C3=F -> 失败(name空)")
    void cg04_NameEmpty() throws Exception {
        var resp = create("CG4_" + System.currentTimeMillis(), "");
        assertNotEquals(0, resp.get("code").asInt());
    }

    @Test @Order(5) @DisplayName("CG05 C4=F -> 失败(name重复)")
    void cg05_NameDup() throws Exception {
        var resp = create("CG5_" + System.currentTimeMillis(), "超级管理员");
        assertNotEquals(0, resp.get("code").asInt());
    }

    @AfterAll
    static void cleanup() {
        for (Long id : createdIds) { try { delete("/user/role/" + id); } catch (Exception ignored) {} }
    }

    static JsonNode create(String code, String name) throws Exception { return post("/user/role", Map.of("code", code, "name", name)); }
    static JsonNode post(String p, Object b) throws Exception { return req("POST", p, b); }
    static JsonNode delete(String p) throws Exception { return req("DELETE", p, null); }
    static JsonNode req(String m, String p, Object b) throws Exception {
        var bd = HttpRequest.newBuilder().uri(URI.create(BASE_URL + p)).header("Content-Type", "application/json").header("Authorization", "Bearer " + jwtToken);
        if (b != null) bd.method(m, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(b)));
        else if ("DELETE".equals(m)) bd.DELETE(); else bd.GET();
        return mapper.readTree(client.send(bd.build(), HttpResponse.BodyHandlers.ofString()).body());
    }
}
