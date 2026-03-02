package com.star.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 课程服务启动类
 * 
 * @author star
 */
@SpringBootApplication(scanBasePackages = {"com.star.course", "com.star.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.star.course.mapper")
@EnableScheduling  // 开启 @Scheduled 定时任务支持，没有这个注解定时任务不会执行
public class CourseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
        System.out.println("===========================================");
        System.out.println("课程服务启动成功！");
        System.out.println("Knife4j文档地址：http://localhost:8077/doc.html");
        System.out.println("===========================================");
    }
}