package com.star.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 分配角色请求
 * 
 * @author star
 */
@Data
@Schema(description = "分配角色请求")
public class AssignRoleRequest {

    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "角色ID", required = true)
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
}
