package com.star.ai.feign;

import com.star.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务 Feign 客户端（供 OpenClaw Skill 根据 username 解析 userId）
 */
@FeignClient(name = "user-service", path = "/user")
public interface UserServiceClient {

    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/by-username")
    R<Object> getByUsername(@RequestParam("username") String username);
}
