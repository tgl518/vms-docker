package cn.yznu.vms.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API请求日志切面（优化版）
 * 1. 智能序列化 - 处理各种复杂对象
 * 2. 完整错误日志 - 包含异常堆栈
 * 3. 分离错误日志文件
 */
@Aspect
@Component
@Slf4j
public class ApiLogAspect {

    private static final String LOG_DIR = "/home/sikadi/code/vedio_project/logs";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    // 配置ObjectMapper支持更多类型
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        // 支持Java8时间类型
        objectMapper.registerModule(new JavaTimeModule());
        // 忽略空值
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 禁止日期转时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 禁止空对象报错
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    // 切入所有Controller层的方法
    @Pointcut("execution(* cn.yznu.vms..controller..*.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();

        // 获取请求信息
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        String clientIp = "unknown";
        String requestUri = "unknown";
        String httpMethod = "unknown";

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                clientIp = getClientIp(request);
                requestUri = request.getRequestURI();
                httpMethod = request.getMethod();
            }
        } catch (Exception e) {
            // 忽略
        }

        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n========== API REQUEST ==========\n");
        logBuilder.append("Time: ").append(now.format(TIME_FORMAT)).append("\n");
        logBuilder.append("URI: ").append(httpMethod).append(" ").append(requestUri).append("\n");
        logBuilder.append("Client IP: ").append(clientIp).append("\n");
        logBuilder.append("Class: ").append(className).append("\n");
        logBuilder.append("Method: ").append(methodName).append("\n");

        // 记录请求参数（智能过滤）
        logBuilder.append("Args: ").append(safeSerialize(filterArgs(args))).append("\n");

        Object result = null;
        String status = "SUCCESS";
        Throwable exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            status = "ERROR";
            exception = e;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            logBuilder.append("Status: ").append(status).append("\n");
            logBuilder.append("Duration: ").append(duration).append("ms\n");

            // 记录错误信息（包含完整堆栈）
            if (exception != null) {
                logBuilder.append("Error: ").append(exception.getClass().getName())
                        .append(": ").append(exception.getMessage()).append("\n");
                logBuilder.append("Stack Trace:\n");
                logBuilder.append(getStackTraceString(exception));
            }

            // 记录响应数据（智能序列化）
            if (result != null && status.equals("SUCCESS")) {
                logBuilder.append("Response: ").append(safeSerialize(result)).append("\n");
            }

            logBuilder.append("==================================\n");

            // 写入日志文件
            writeToLogFile(now, logBuilder.toString(), status.equals("ERROR"));

            // 同时输出到控制台
            if (status.equals("ERROR")) {
                log.error(logBuilder.toString());
            } else {
                log.debug(logBuilder.toString());
            }
        }
    }

    /**
     * 智能序列化 - 处理各种复杂对象
     */
    private String safeSerialize(Object obj) {
        if (obj == null) return "null";

        try {
            String json = objectMapper.writeValueAsString(obj);
            return truncate(json, 2000);
        } catch (JsonProcessingException e) {
            // 尝试简化处理
            return simplifyObject(obj);
        }
    }

    /**
     * 简化复杂对象为可读字符串
     */
    private String simplifyObject(Object obj) {
        if (obj == null) return "null";

        Class<?> clazz = obj.getClass();
        String simpleName = clazz.getSimpleName();

        // 处理常见不可序列化类型
        if (obj instanceof InputStream) {
            return "[InputStream]";
        }
        if (obj instanceof OutputStream) {
            return "[OutputStream]";
        }
        if (obj instanceof HttpServletRequest) {
            return "[HttpServletRequest]";
        }
        if (obj instanceof HttpServletResponse) {
            return "[HttpServletResponse]";
        }
        if (obj instanceof MultipartFile) {
            MultipartFile file = (MultipartFile) obj;
            return String.format("[MultipartFile: name=%s, size=%d]",
                    file.getOriginalFilename(), file.getSize());
        }
        if (obj instanceof byte[]) {
            return "[byte[]: length=" + ((byte[]) obj).length + "]";
        }

        // 尝试通过toString获取信息
        try {
            String str = obj.toString();
            // 避免默认的Object.toString()
            if (str.contains("@") && str.startsWith(clazz.getName())) {
                return "[" + simpleName + "]";
            }
            return truncate(str, 500);
        } catch (Exception e) {
            return "[" + simpleName + ": 序列化失败]";
        }
    }

    /**
     * 过滤请求参数
     */
    private Object filterArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";

        Object[] filtered = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                filtered[i] = null;
            } else if (arg instanceof HttpServletRequest) {
                filtered[i] = "[HttpServletRequest]";
            } else if (arg instanceof HttpServletResponse) {
                filtered[i] = "[HttpServletResponse]";
            } else if (arg instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) arg;
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("type", "MultipartFile");
                info.put("name", file.getOriginalFilename());
                info.put("size", file.getSize());
                filtered[i] = info;
            } else if (arg instanceof InputStream) {
                filtered[i] = "[InputStream]";
            } else if (arg instanceof byte[]) {
                filtered[i] = "[byte[]: " + ((byte[]) arg).length + " bytes]";
            } else {
                // 过滤密码字段
                filtered[i] = filterSensitiveFields(arg);
            }
        }
        return filtered;
    }

    /**
     * 过滤敏感字段（如密码）
     */
    private Object filterSensitiveFields(Object arg) {
        if (arg == null) return null;

        try {
            String json = objectMapper.writeValueAsString(arg);
            // 简单的密码脱敏
            json = json.replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"******\"");
            json = json.replaceAll("\"oldPassword\"\\s*:\\s*\"[^\"]*\"", "\"oldPassword\":\"******\"");
            json = json.replaceAll("\"newPassword\"\\s*:\\s*\"[^\"]*\"", "\"newPassword\":\"******\"");
            json = json.replaceAll("\"confirmPassword\"\\s*:\\s*\"[^\"]*\"", "\"confirmPassword\":\"******\"");
            return json;
        } catch (Exception e) {
            return arg.toString();
        }
    }

    /**
     * 获取异常堆栈字符串
     */
    private String getStackTraceString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 写入日志文件
     *
     * @param isError 是否为错误日志（错误单独写一份）
     */
    private void writeToLogFile(LocalDateTime now, String content, boolean isError) {
        try {
            Path logDir = Paths.get(LOG_DIR);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }

            String dateStr = now.format(DATE_FORMAT);

            // 写入主日志
            String fileName = "api-" + dateStr + ".log";
            Path logFile = logDir.resolve(fileName);
            Files.write(logFile, content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            // 错误日志单独保存一份
            if (isError) {
                String errorFileName = "error-" + dateStr + ".log";
                Path errorLogFile = logDir.resolve(errorFileName);
                Files.write(errorLogFile, content.getBytes(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            log.error("写入日志文件失败: {}", e.getMessage());
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 截断过长的字符串
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength) + "...[truncated]";
    }
}
