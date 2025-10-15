package com.star.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.common.dto.PageParam;
import com.star.common.dto.PageResult;
import com.star.order.entity.UserCourse;

/**
 * 用户课程关系服务接口
 * 
 * @author star
 */
public interface UserCourseService extends IService<UserCourse> {

    /**
     * 创建用户课程关系
     */
    Boolean createUserCourse(Long userId, Long courseId);

    /**
     * 从订单开通用户课程
     */
    Boolean activateUserCoursesFromOrder(Long orderId);

    /**
     * 检查用户是否已购买课程
     */
    Boolean checkUserHasCourse(Long userId, Long courseId);

    /**
     * 查询用户购买的课程
     */
    PageResult<UserCourse> getUserCourses(Long userId, PageParam pageParam);

    /**
     * 更新学习进度
     */
    Boolean updateProgress(Long userId, Long courseId, Integer progress);

    /**
     * 统计用户购买的课程数量
     */
    Integer countUserCourses(Long userId);

    /**
     * 统计课程购买人数
     */
    Integer countCourseUsers(Long courseId);
}
