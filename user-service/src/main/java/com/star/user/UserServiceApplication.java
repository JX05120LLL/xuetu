package com.star.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 用户服务启动类
 * 
 * 功能说明：
 * 1. 用户注册、登录、认证
 * 2. 用户信息管理
 * 3. 角色权限管理
 * 4. JWT Token生成和验证
 * 
 * @author star
 */
@SpringBootApplication(scanBasePackages = {"com.star.user", "com.star.common"})
@EnableDiscoveryClient  // 启用服务发现
@EnableFeignClients     // 启用Feign客户端
@MapperScan("com.star.user.mapper")  // 扫描Mapper接口
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("=================================");
        System.out.println("    访问地址: http://localhost:8088");
        System.out.println("=================================");
    }
}