package com.star.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.learning.entity.UserCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户课程Mapper接口
 *
 * @author star
 */
@Mapper
public interface UserCourseMapper extends BaseMapper<UserCourse> {

    /**
     * 更新课程学习进度
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param progress 进度值(0-100)
     * @return 更新的记录数
     */
    @Update("UPDATE user_course SET progress = #{progress}, last_learn_time = NOW() " +
            "WHERE user_id = #{userId} AND course_id = #{courseId} AND status = 1")
    int updateProgress(@Param("userId") Long userId, @Param("courseId") Long courseId, @Param("progress") Integer progress);
    
    /**
     * 更新课程总体进度
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param progress 进度值(0-100)
     * @return 更新的记录数
     */
    @Update("UPDATE user_course SET progress = #{progress}, last_learn_time = NOW() " +
            "WHERE user_id = #{userId} AND course_id = #{courseId} AND status = 1")
    int updateCourseProgress(@Param("userId") Long userId, @Param("courseId") Long courseId, @Param("progress") Integer progress);

    /**
     * 检查用户是否已购买课程
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 查询结果
     */
    default boolean checkUserHasCourse(Long userId, Long courseId) {
        UserCourse course = selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserCourse>()
                .eq(UserCourse::getUserId, userId)
                .eq(UserCourse::getCourseId, courseId)
                .eq(UserCourse::getStatus, UserCourse.Status.ACTIVE.getValue())
        );
        return course != null;
    }

    /**
     * 查询用户所有课程的平均进度
     *
     * @param userId 用户ID
     * @return 平均进度(0-100)
     */
    @org.apache.ibatis.annotations.Select("SELECT AVG(progress) FROM user_course WHERE user_id = #{userId} AND status = 1")
    Double selectAverageProgressByUserId(@Param("userId") Long userId);

    /**
     * 统计用户的总课程数
     *
     * @param userId 用户ID
     * @return 总课程数
     */
    @org.apache.ibatis.annotations.Select("SELECT COUNT(*) FROM user_course WHERE user_id = #{userId} AND status = 1")
    Integer countTotalCoursesByUserId(@Param("userId") Long userId);

    /**
     * 统计用户已完成的课程数（进度=100%）
     *
     * @param userId 用户ID
     * @return 已完成课程数
     */
    @org.apache.ibatis.annotations.Select("SELECT COUNT(*) FROM user_course WHERE user_id = #{userId} AND status = 1 AND progress = 100")
    Integer countCompletedCoursesByUserId(@Param("userId") Long userId);
}