package com.star.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author star
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND status = 1")
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE email = #{email} AND status = 1")
    User findByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} AND status = 1")
    User findByPhone(@Param("phone") String phone);

    /**
     * 根据用户名、邮箱或手机号查询用户（登录用）
     */
    @Select("SELECT * FROM user WHERE (username = #{identifier} OR email = #{identifier} OR phone = #{identifier}) AND status = 1")
    User findByIdentifier(@Param("identifier") String identifier);

    /**
     * 查询用户的角色列表
     */
    @Select("SELECT r.role_name FROM user_role ur " +
            "LEFT JOIN role r ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId}")
    List<String> findUserRoles(@Param("userId") Long userId);

    /**
     * 查询用户的权限列表
     */
    @Select("SELECT DISTINCT p.permission_key FROM user_role ur " +
            "LEFT JOIN role_permission rp ON ur.role_id = rp.role_id " +
            "LEFT JOIN permission p ON rp.permission_id = p.id " +
            "WHERE ur.user_id = #{userId}")
    List<String> findUserPermissions(@Param("userId") Long userId);
}