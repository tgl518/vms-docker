package cn.yznu.vms.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * VMS File 服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("cn.yznu.vms.file.mapper")
@ComponentScan(basePackages = {"cn.yznu.vms.common", "cn.yznu.vms.file"})
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
        System.out.println("============================================");
        System.out.println("    VMS File 服务启动成功!");
        System.out.println("    端口: 8085");
        System.out.println("    API文档: http://localhost:8085/doc.html");
        System.out.println("============================================");
    }
}
