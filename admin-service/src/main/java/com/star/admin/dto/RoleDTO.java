package com.star.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色DTO
 * 
 * @author star
 */
@Data
@Schema(description = "角色信息")
public class RoleDTO implements Serializable {

    @Schema(description = "角色ID")
    private Long id;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "权限列表")
    private List<PermissionDTO> permissions;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
