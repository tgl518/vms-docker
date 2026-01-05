package cn.yznu.vms.video;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * VMS Video 服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableFeignClients(basePackages = "cn.yznu.vms.video.feign")
@MapperScan("cn.yznu.vms.video.mapper")
@ComponentScan(basePackages = {"cn.yznu.vms.common", "cn.yznu.vms.video"})
public class VideoApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoApplication.class, args);
        System.out.println("============================================");
        System.out.println("    VMS Video 服务启动成功!");
        System.out.println("    端口: 8082");
        System.out.println("    API文档: http://localhost:8082/doc.html");
        System.out.println("============================================");
    }
}
