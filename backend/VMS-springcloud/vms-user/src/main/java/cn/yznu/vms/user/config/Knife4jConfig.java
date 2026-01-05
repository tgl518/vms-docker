package cn.yznu.vms.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j/Swagger 配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VMS 用户服务 API")
                        .version("1.0.0")
                        .description("视频管理系统 - 用户服务接口文档")
                        .contact(new Contact()
                                .name("VMS Team")
                                .email("vms@example.com")));
    }
}
