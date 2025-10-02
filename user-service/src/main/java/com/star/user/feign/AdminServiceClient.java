package com.star.user.feign;

import com.star.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Admin服务Feign客户端
 * 
 * @author star
 */
@FeignClient(name = "admin-service")
public interface AdminServiceClient {

    /**
     * 获取用户角色名称列表
     */
    @GetMapping("/role/user/{userId}")
    R<List<RoleDTO>> getUserRoles(@PathVariable("userId") Long userId);

    /**
     * 获取用户权限Key列表
     */
    @GetMapping("/permission/user/{userId}/keys")
    R<List<String>> getUserPermissionKeys(@PathVariable("userId") Long userId);
}
