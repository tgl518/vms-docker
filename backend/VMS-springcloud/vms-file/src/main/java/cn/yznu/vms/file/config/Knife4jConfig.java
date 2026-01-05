package cn.yznu.vms.file.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VMS File 服务 API")
                        .version("1.0.0")
                        .description("视频管理系统 - File服务接口文档")
                        .contact(new Contact().name("VMS Team")));
    }
}
