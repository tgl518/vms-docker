package cn.yznu.vms.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关全局异常处理器
 * 处理路由转发失败、服务不可用等异常
 */
@Slf4j
@Order(-1)  // 优先级最高
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        int code;
        String message;

        // 根据异常类型返回不同的错误信息
        if (ex instanceof NotFoundException) {
            code = 503;
            message = "服务暂时不可用，请稍后重试";
            log.error("服务不可用: {}", ex.getMessage());
        } else if (ex instanceof ResponseStatusException rse) {
            code = rse.getStatusCode().value();
            message = rse.getReason() != null ? rse.getReason() : "请求处理失败";
            log.error("响应异常: {} - {}", code, message);
        } else {
            code = 500;
            message = "网关内部错误";
            log.error("网关异常: ", ex);
        }

        response.setStatusCode(HttpStatus.valueOf(code >= 400 && code < 600 ? code : 500));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = String.format(
                "{\"code\":%d,\"message\":\"%s\",\"data\":null,\"timestamp\":%d}",
                code, message, System.currentTimeMillis()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
