package com.star.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.learning.entity.UserCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户课程关系Mapper
 * 
 * @author star
 */
@Mapper
public interface UserCourseMapper extends BaseMapper<UserCourse> {

    /**
     * 分页查询用户课程
     */
    IPage<UserCourse> selectUserCoursesPage(Page<UserCourse> page, 
                                           @Param("userId") Long userId, 
                                           @Param("status") Integer status);

    /**
     * 检查用户是否拥有课程
     */
    UserCourse selectByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 查询用户所有课程
     */
    List<UserCourse> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询用户正在学习的课程
     */
    List<UserCourse> selectLearningCoursesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户已完成的课程
     */
    List<UserCourse> selectCompletedCoursesByUserId(@Param("userId") Long userId);

    /**
     * 更新课程学习进度
     */
    int updateCourseProgress(@Param("userId") Long userId, 
                            @Param("courseId") Long courseId, 
                            @Param("progress") Integer progress);

    /**
     * 激活用户课程（订单支付后调用）
     */
    int activateCourse(@Param("userId") Long userId, 
                      @Param("courseId") Long courseId, 
                      @Param("orderId") Long orderId);

    /**
     * 查询过期课程
     */
    List<UserCourse> selectExpiredCourses();

    /**
     * 批量更新课程状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
}