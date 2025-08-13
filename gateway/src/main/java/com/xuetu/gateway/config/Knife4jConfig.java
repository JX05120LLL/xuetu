package com.xuetu.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 网关聚合各微服务的API文档配置
 * 
 * 现在主要通过 application.yml 配置文件实现聚合
 * 访问地址：http://localhost:8080/doc.html
 * 
 * @author star
 */
@Slf4j
@Configuration
public class Knife4jConfig {
    
    public Knife4jConfig() {
        log.info("网关Knife4j配置已加载，通过配置文件实现文档聚合");
    }
}