package com.star.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 授权请求
 * 
 * @author star
 */
@Data
@Schema(description = "角色授权请求")
public class GrantPermissionRequest {

    @Schema(description = "角色ID", required = true)
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "权限ID列表", required = true)
    @NotEmpty(message = "权限列表不能为空")
    private List<Long> permissionIds;
}
