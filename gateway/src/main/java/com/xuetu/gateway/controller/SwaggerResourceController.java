package com.xuetu.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger资源控制器
 * 处理Knife4j聚合文档的路由发现
 * 
 * @author star
 */
@Slf4j
@RestController
public class SwaggerResourceController {

    private final RouteDefinitionRepository routeDefinitionRepository;

    @Value("${spring.application.name:gateway}")
    private String gatewayName;

    public SwaggerResourceController(RouteDefinitionRepository routeDefinitionRepository) {
        this.routeDefinitionRepository = routeDefinitionRepository;
    }

    /**
     * 提供Swagger资源列表
     * Knife4j通过这个接口获取所有微服务的API文档
     */
    @GetMapping("/swagger-resources")
    public Mono<List<SwaggerResource>> swaggerResources() {
        log.info("获取Swagger资源列表");
        
        List<SwaggerResource> resources = new ArrayList<>();
        
        // 手动添加各个微服务的API文档资源
        resources.add(createSwaggerResource("用户服务", "/api/user/v3/api-docs", "1.0"));
        resources.add(createSwaggerResource("课程服务", "/api/course/v3/api-docs", "1.0"));
        resources.add(createSwaggerResource("订单服务", "/api/order/v3/api-docs", "1.0"));
        resources.add(createSwaggerResource("学习服务", "/api/learning/v3/api-docs", "1.0"));
        
        log.info("返回{}个Swagger资源", resources.size());
        return Mono.just(resources);
    }

    /**
     * 自动发现路由中的API文档资源
     */
    @GetMapping("/swagger-resources/auto")
    public Mono<List<SwaggerResource>> autoSwaggerResources() {
        log.info("自动发现Swagger资源");
        
        return routeDefinitionRepository.getRouteDefinitions()
                .collectList()
                .map(this::buildSwaggerResources);
    }

    /**
     * 从路由定义构建Swagger资源列表
     */
    private List<SwaggerResource> buildSwaggerResources(List<RouteDefinition> routeDefinitions) {
        List<SwaggerResource> resources = new ArrayList<>();
        
        for (RouteDefinition routeDefinition : routeDefinitions) {
            String routeId = routeDefinition.getId();
            
            // 只处理服务路由，跳过文档路由
            if (routeId.endsWith("-service") && !routeId.endsWith("-docs")) {
                String serviceName = routeId.replace("-service", "");
                String location = "/api/" + serviceName + "/v3/api-docs";
                
                resources.add(createSwaggerResource(
                    getServiceDisplayName(serviceName), 
                    location, 
                    "1.0"
                ));
            }
        }
        
        return resources;
    }

    /**
     * 创建Swagger资源
     */
    private SwaggerResource createSwaggerResource(String name, String location, String version) {
        SwaggerResource resource = new SwaggerResource();
        resource.setName(name);
        resource.setLocation(location);
        resource.setSwaggerVersion(version);
        return resource;
    }

    /**
     * 获取服务显示名称
     */
    private String getServiceDisplayName(String serviceName) {
        switch (serviceName) {
            case "user":
                return "用户服务";
            case "course":
                return "课程服务";
            case "order":
                return "订单服务";
            case "learning":
                return "学习服务";
            default:
                return serviceName + "服务";
        }
    }

    /**
     * Swagger资源实体类
     */
    public static class SwaggerResource {
        private String name;
        private String location;
        private String swaggerVersion;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getSwaggerVersion() {
            return swaggerVersion;
        }

        public void setSwaggerVersion(String swaggerVersion) {
            this.swaggerVersion = swaggerVersion;
        }
    }
}