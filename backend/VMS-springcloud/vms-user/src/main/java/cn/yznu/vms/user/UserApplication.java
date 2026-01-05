package cn.yznu.vms.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * VMS 用户服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@MapperScan("cn.yznu.vms.user.mapper")
@ComponentScan(basePackages = {"cn.yznu.vms.common", "cn.yznu.vms.user"})
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        System.out.println("============================================");
        System.out.println("    VMS User 用户服务启动成功!");
        System.out.println("    端口: 8081");
        System.out.println("    API文档: http://localhost:8081/doc.html");
        System.out.println("============================================");
    }
}

