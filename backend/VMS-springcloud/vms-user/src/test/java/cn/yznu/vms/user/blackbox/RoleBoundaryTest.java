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
 * 角色管理 - 边界值测试 (10个用例)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleBoundaryTest {

    static final String BASE_URL = "http://localhost:8080";
    static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
    static final ObjectMapper mapper = new ObjectMapper();
    static String jwtToken;
    static final List<Long> createdIds = new ArrayList<>();
    static final String TS = String.valueOf(System.currentTimeMillis()); // 唯一时间戳

    @BeforeAll
    static void login() throws Exception {
        var req = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/user/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(Map.of("username", "admin", "password", "123456"))))
            .build();
        jwtToken = mapper.readTree(client.send(req, HttpResponse.BodyHandlers.ofString()).body()).get("data").get("accessToken").asText();
    }

    @Test @Order(1) @DisplayName("BV01 code最小长度1")
    void bv01() throws Exception {
        // code最小1字符，使用唯一值
        var resp = create("A" + TS.substring(0,3), "边界1_" + TS);
        assertEquals(0, resp.get("code").asInt(), () -> resp.toString());
        if (resp.get("data") != null) createdIds.add(resp.get("data").asLong());
    }

    @Test @Order(2) @DisplayName("BV02 code最大长度50")
    void bv02() throws Exception {
        // code正好50字符
        String code50 = ("BV2" + TS).substring(0, Math.min(13, ("BV2" + TS).length())) + "X".repeat(37);
        var resp = create(code50.substring(0, 50), "边界2_" + TS);
        assertEquals(0, resp.get("code").asInt(), () -> resp.toString());
        if (resp.get("data") != null) createdIds.add(resp.get("data").asLong());
    }

    @Test @Order(3) @DisplayName("BV03 code超长51(失败)")
    void bv03() throws Exception {
        var resp = create("C".repeat(51), "边界3_" + TS);
        assertNotEquals(0, resp.get("code").asInt(), "超长code应该被拒绝");
    }

    @Test @Order(4) @DisplayName("BV04 name最小长度1")
    void bv04() throws Exception {
        var resp = create("BV4_" + TS, "角");
        assertEquals(0, resp.get("code").asInt(), () -> resp.toString());
        if (resp.get("data") != null) createdIds.add(resp.get("data").asLong());
    }

    @Test @Order(5) @DisplayName("BV05 name最大长度100")
    void bv05() throws Exception {
        var resp = create("BV5_" + TS, "名".repeat(100));
        assertEquals(0, resp.get("code").asInt(), () -> resp.toString());
        if (resp.get("data") != null) createdIds.add(resp.get("data").asLong());
    }

    @Test @Order(6) @DisplayName("BV06 name超长101(失败)")
    void bv06() throws Exception {
        var resp = create("BV6_" + TS, "名".repeat(101));
        assertNotEquals(0, resp.get("code").asInt(), "超长name应该被拒绝");
    }

    @Test @Order(7) @DisplayName("BV07 sort=0")
    void bv07() throws Exception {
        var resp = post("/user/role", Map.of("code", "BV7_" + TS, "name", "排序0_" + TS, "sort", 0));
        assertEquals(0, resp.get("code").asInt(), () -> resp.toString());
        if (resp.get("data") != null) createdIds.add(resp.get("data").asLong());
    }

    @Test @Order(8) @DisplayName("BV08 sort=-1")
    void bv08() throws Exception {
        var resp = post("/user/role", Map.of("code", "BV8_" + TS, "name", "排序负_" + TS, "sort", -1));
        assertEquals(0, resp.get("code").asInt(), () -> resp.toString());
        if (resp.get("data") != null) createdIds.add(resp.get("data").asLong());
    }

    @Test @Order(9) @DisplayName("BV09 查询id=1")
    void bv09() throws Exception {
        assertEquals(0, get("/user/role/1").get("code").asInt());
    }

    @Test @Order(10) @DisplayName("BV10 查询id=99999(不存在)")
    void bv10() throws Exception {
        assertEquals(404, get("/user/role/99999").get("code").asInt());
    }

    @AfterAll
    static void cleanup() {
        System.out.println("清理测试数据: " + createdIds);
        for (Long id : createdIds) {
            try { delete("/user/role/" + id); } catch (Exception e) { System.out.println("清理失败: " + id); }
        }
    }

    static JsonNode create(String code, String name) throws Exception { return post("/user/role", Map.of("code", code, "name", name)); }
    static JsonNode get(String p) throws Exception { return req("GET", p, null); }
    static JsonNode post(String p, Object b) throws Exception { return req("POST", p, b); }
    static JsonNode delete(String p) throws Exception { return req("DELETE", p, null); }
    static JsonNode req(String m, String p, Object b) throws Exception {
        var bd = HttpRequest.newBuilder().uri(URI.create(BASE_URL + p)).header("Content-Type", "application/json").header("Authorization", "Bearer " + jwtToken);
        if (b != null) bd.method(m, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(b)));
        else if ("DELETE".equals(m)) bd.DELETE(); else bd.GET();
        return mapper.readTree(client.send(bd.build(), HttpResponse.BodyHandlers.ofString()).body());
    }
}
