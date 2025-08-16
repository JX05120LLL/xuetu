package com.star.order.feign;

import com.star.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务Feign客户端
 * 
 * @author star
 */
@FeignClient(name = "user-service", path = "/api")
public interface UserServiceClient {

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/users/{userId}")
    R<Object> getUserById(@PathVariable("userId") Long userId);

    /**
     * 检查用户是否存在
     */
    @GetMapping("/users/{userId}/exists")
    R<Boolean> checkUserExists(@PathVariable("userId") Long userId);
}