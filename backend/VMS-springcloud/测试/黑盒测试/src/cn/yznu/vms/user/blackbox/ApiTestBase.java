package cn.yznu.vms.user.blackbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

/**
 * 黑盒测试基础类 - 提供HTTP请求和断言方法
 */
public class ApiTestBase {

    protected static final String BASE_URL = "http://localhost:8080";
    protected static final Long ADMIN_USER_ID = 1L;
    
    protected final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
    protected final ObjectMapper mapper = new ObjectMapper();

    // ===== HTTP方法 =====
    
    protected JsonNode get(String path) throws Exception {
        return request("GET", path, null);
    }
    
    protected JsonNode post(String path, Object body) throws Exception {
        return request("POST", path, body);
    }
    
    protected JsonNode patch(String path, Object body) throws Exception {
        return request("PATCH", path, body);
    }
    
    protected JsonNode delete(String path) throws Exception {
        return request("DELETE", path, null);
    }
    
    private JsonNode request(String method, String path, Object body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + path))
            .header("Content-Type", "application/json")
            .header("X-User-Id", String.valueOf(ADMIN_USER_ID));
        
        if (body != null) {
            builder.method(method, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
        } else if ("DELETE".equals(method)) {
            builder.DELETE();
        } else {
            builder.GET();
        }
        
        HttpResponse<String> resp = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(resp.body());
    }

    // ===== 断言方法 =====
    
    protected void assertOk(JsonNode resp, String msg) {
        int code = resp.get("code").asInt();
        if (code == 0) {
            System.out.println("✅ " + msg);
        } else {
            System.out.println("❌ " + msg + " (code=" + code + ", " + resp.get("message").asText() + ")");
        }
    }
    
    protected void assertFail(JsonNode resp, String msg) {
        int code = resp.get("code").asInt();
        if (code != 0) {
            System.out.println("✅ " + msg + " (预期失败: " + resp.get("message").asText() + ")");
        } else {
            System.out.println("❌ " + msg + " (应该失败但成功了)");
        }
    }
}
