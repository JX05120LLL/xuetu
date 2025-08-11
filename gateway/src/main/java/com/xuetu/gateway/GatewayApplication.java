package com.xuetu.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务启动类
 * 
 * 功能说明：
 * 1. 作为微服务架构的统一入口
 * 2. 负责请求路由转发
 * 3. 统一认证和鉴权
 * 4. 跨域处理
 * 5. 限流和熔断
 * 
 * @author star
 */
@SpringBootApplication
@EnableDiscoveryClient  // 启用服务发现，注册到Nacos
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("=================================");
        System.out.println("    网关服务启动成功！");
        System.out.println("    访问地址: http://localhost:8080");
        System.out.println("=================================");
    }
}