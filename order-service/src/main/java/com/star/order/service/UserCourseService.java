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
     * 查询用户购买的课程列表
     * 
     * @param userId 用户ID
     * @param pageParam 分页参数
     * @return 课程列表
     */
    PageResult<UserCourse> getUserCourses(Long userId, PageParam pageParam);

    /**
     * 检查用户是否已购买课程
     * 
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否已购买
     */
    Boolean checkUserHasCourse(Long userId, Long courseId);

    /**
     * 添加用户课程关系（购买后调用）
     * 
     * @param userCourse 用户课程关系
     * @return 是否成功
     */
    Boolean addUserCourse(UserCourse userCourse);

    /**
     * 更新学习进度
     * 
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param progress 进度百分比
     * @return 是否成功
     */
    Boolean updateProgress(Long userId, Long courseId, Integer progress);

    /**
     * 统计用户购买的课程数量
     * 
     * @param userId 用户ID
     * @return 课程数量
     */
    Integer countUserCourses(Long userId);

    /**
     * 查询课程的购买用户数量
     * 
     * @param courseId 课程ID
     * @return 购买用户数量
     */
    Integer countCourseUsers(Long courseId);

    /**
     * 处理订单支付成功后的课程开通
     * 
     * @param orderId 订单ID
     * @return 是否成功
     */
    Boolean activateUserCoursesFromOrder(Long orderId);
}