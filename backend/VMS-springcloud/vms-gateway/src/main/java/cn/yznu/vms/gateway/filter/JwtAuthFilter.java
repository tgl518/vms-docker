package cn.yznu.vms.gateway.filter;

import cn.yznu.vms.gateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * JWT 认证全局过滤器
 * 拦截所有请求，校验 Token 有效性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtProperties jwtProperties;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 1. 检查是否是白名单路径
        if (isIgnoreUrl(path)) {
            log.debug("白名单路径，跳过认证: {}", path);
            return chain.filter(exchange);
        }

        // 2. 获取 Token
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            log.warn("请求无 Token: {}", path);
            return unauthorizedResponse(exchange, "未登录或 Token 缺失");
        }

        String token = authHeader.substring(TOKEN_PREFIX.length());

        // 3. 解析并校验 Token
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecret())
                    .parseClaimsJws(token)
                    .getBody();

            // 4. 提取用户信息，传递给下游服务
            Object userIdObj = claims.get("userId");
            String userId = userIdObj != null ? userIdObj.toString() : "";
            String username = claims.getSubject();
            
            // 从roles列表中获取角色信息，支持多角色传递
            Object rolesObj = claims.get("roles");
            String roleStr = "user"; // 默认普通用户
            if (rolesObj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<String> roles = (java.util.List<String>) rolesObj;
                if (!roles.isEmpty()) {
                    roleStr = String.join(",", roles);
                }
            }

            log.debug("Token 校验成功: userId={}, username={}, roles={}", userId, username, roleStr);

            // 5. 将用户信息添加到请求头，传递给下游微服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(USER_ID_HEADER, userId)
                    .header(USERNAME_HEADER, username)
                    .header(USER_ROLE_HEADER, roleStr)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (ExpiredJwtException e) {
            log.warn("Token 已过期: {}", path);
            return unauthorizedResponse(exchange, "Token 已过期，请重新登录");
        } catch (Exception e) {
            log.warn("Token 无效: {} - {}", path, e.getMessage());
            return unauthorizedResponse(exchange, "Token 无效");
        }
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isIgnoreUrl(String path) {
        return jwtProperties.getIgnoreUrls().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 返回 401 未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = String.format(
                "{\"code\":401,\"message\":\"%s\",\"data\":null,\"timestamp\":%d}",
                message, System.currentTimeMillis()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // 优先级较高，在其他过滤器之前执行
        return -100;
    }
}
