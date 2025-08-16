package com.star.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.order.entity.UserCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户课程关系数据访问层
 * 
 * @author star
 */
@Mapper
public interface UserCourseMapper extends BaseMapper<UserCourse> {

    /**
     * 查询用户购买的课程列表
     */
    @Select("SELECT * FROM user_course WHERE user_id = #{userId} AND status = 1 ORDER BY purchase_time DESC")
    List<UserCourse> selectByUserId(@Param("userId") Long userId);

    /**
     * 检查用户是否已购买课程
     */
    @Select("SELECT COUNT(*) FROM user_course WHERE user_id = #{userId} AND course_id = #{courseId} AND status = 1")
    Integer checkUserHasCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 根据用户ID和课程ID查询记录
     */
    @Select("SELECT * FROM user_course WHERE user_id = #{userId} AND course_id = #{courseId} LIMIT 1")
    UserCourse selectByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 更新学习进度
     */
    @Update("UPDATE user_course SET progress = #{progress}, last_learn_time = NOW(), updated_time = NOW() " +
            "WHERE user_id = #{userId} AND course_id = #{courseId}")
    Integer updateProgress(@Param("userId") Long userId, @Param("courseId") Long courseId, @Param("progress") Integer progress);

    /**
     * 统计用户购买的课程数量
     */
    @Select("SELECT COUNT(*) FROM user_course WHERE user_id = #{userId} AND status = 1")
    Integer countByUserId(@Param("userId") Long userId);

    /**
     * 查询课程的购买用户数量
     */
    @Select("SELECT COUNT(*) FROM user_course WHERE course_id = #{courseId} AND status = 1")
    Integer countByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询即将过期的课程
     */
    @Select("SELECT * FROM user_course WHERE expire_time IS NOT NULL AND expire_time > NOW() " +
            "AND expire_time < DATE_ADD(NOW(), INTERVAL #{days} DAY) AND status = 1")
    List<UserCourse> selectExpiringSoon(@Param("days") Integer days);
}