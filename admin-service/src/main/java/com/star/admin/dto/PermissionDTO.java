package com.star.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 权限DTO
 * 
 * @author star
 */
@Data
@Schema(description = "权限信息")
public class PermissionDTO implements Serializable {

    @Schema(description = "权限ID")
    private Long id;

    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "权限标识")
    private String permissionKey;

    @Schema(description = "请求URL")
    private String url;

    @Schema(description = "权限描述")
    private String description;
}
