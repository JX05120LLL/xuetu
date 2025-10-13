package com.xuetu.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

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
 * 注意：本地开发模式不使用Nacos服务发现
 * 
 * @author star
 */
@SpringBootApplication
// @EnableDiscoveryClient  // 本地开发模式禁用，生产环境启用
public class GatewayApplication {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   网关准备启动...");
        System.out.println("========================================");
        
        ConfigurableApplicationContext context = SpringApplication.run(GatewayApplication.class, args);
        
        // 打印活动的配置文件
        String[] profiles = context.getEnvironment().getActiveProfiles();
        System.out.println("========================================");
        System.out.println("   活动配置: " + (profiles.length > 0 ? Arrays.toString(profiles) : "default"));
        System.out.println("========================================");
        
        // 检查Bean是否被加载
        String[] filterBeans = context.getBeanNamesForType(org.springframework.cloud.gateway.filter.GlobalFilter.class);
        System.out.println("========================================");
        System.out.println("   已加载的过滤器: " + filterBeans.length + "个");
        for (String bean : filterBeans) {
            System.out.println("   - " + bean);
        }
        System.out.println("========================================");
        
        System.out.println("=================================");
        System.out.println("    网关服务启动成功！");
        System.out.println("    访问地址: http://localhost:8080");
        System.out.println("=================================");
    }
}