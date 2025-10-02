package com.star.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * AI服务启动类
 * 
 * @author star
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.star.ai.mapper")
@ComponentScan(basePackages = {"com.star.ai", "com.star.common"})
public class AiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiServiceApplication.class, args);
        System.out.println("=================================");
        System.out.println("🤖 AI服务启动成功！");
        System.out.println("📚 API文档地址: http://localhost:8066/doc.html");
        System.out.println("🌐 健康检查: http://localhost:8066/actuator/health");
        System.out.println("=================================");
    }
}
