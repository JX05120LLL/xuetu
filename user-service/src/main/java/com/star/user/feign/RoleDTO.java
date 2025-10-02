package com.star.user.feign;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色DTO（用于Feign调用）
 * 
 * @author star
 */
@Data
public class RoleDTO implements Serializable {
    
    private Long id;
    private String roleName;
    private String description;
}
