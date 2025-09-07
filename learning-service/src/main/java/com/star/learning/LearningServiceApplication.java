package com.star.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 学习服务启动类
 * 
 * @author star
 */
@SpringBootApplication(scanBasePackages = {"com.star.learning", "com.star.common"})
@EnableDiscoveryClient  // 启用服务发现，注册到Nacos
@EnableFeignClients
public class LearningServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningServiceApplication.class, args);
        System.out.println("=================================");
        System.out.println("    学习服务启动成功！");
        System.out.println("    服务端口: 8044");
        System.out.println("    API文档: http://localhost:8044/doc.html");
        System.out.println("    服务已注册到Nacos: learning-service");
        System.out.println("=================================");
    }
}