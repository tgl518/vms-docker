package cn.yznu.vms.interaction;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * VMS Interaction 服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.yznu.vms.api.client")
@MapperScan("cn.yznu.vms.interaction.mapper")
@ComponentScan(basePackages = {"cn.yznu.vms.common", "cn.yznu.vms.interaction"})
public class InteractionApplication {

    public static void main(String[] args) {
        SpringApplication.run(InteractionApplication.class, args);
        System.out.println("============================================");
        System.out.println("    VMS Interaction 服务启动成功!");
        System.out.println("    端口: 8083");
        System.out.println("    API文档: http://localhost:8083/doc.html");
        System.out.println("============================================");
    }
}
