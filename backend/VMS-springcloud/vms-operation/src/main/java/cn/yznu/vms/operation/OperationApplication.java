package cn.yznu.vms.operation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * VMS Operation 服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.yznu.vms.operation.feign")
@MapperScan("cn.yznu.vms.operation.mapper")
@ComponentScan(basePackages = {"cn.yznu.vms.common", "cn.yznu.vms.operation"})
public class OperationApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperationApplication.class, args);
        System.out.println("============================================");
        System.out.println("    VMS Operation 服务启动成功!");
        System.out.println("    端口: 8084");
        System.out.println("    API文档: http://localhost:8084/doc.html");
        System.out.println("============================================");
    }
}
