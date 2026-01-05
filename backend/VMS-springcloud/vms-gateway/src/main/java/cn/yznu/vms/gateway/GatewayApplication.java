package cn.yznu.vms.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * VMS API 网关启动类
 * 负责路由转发、跨域处理、JWT 校验等
 */
@SpringBootApplication
@EnableDiscoveryClient  // 启用 Nacos 服务发现
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("============================================");
        System.out.println("    VMS Gateway 网关服务启动成功!");
        System.out.println("    端口: 8080");
        System.out.println("============================================");
    }
}
