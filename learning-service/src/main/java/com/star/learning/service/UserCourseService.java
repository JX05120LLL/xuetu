package com.star.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.common.dto.PageParam;
import com.star.common.dto.PageResult;
import com.star.common.mq.OrderPaidMessage;
import com.star.learning.dto.UserCourseDTO;
import com.star.learning.entity.UserCourse;

/**
 * 用户课程服务接口
 *
 * @author star
 */
public interface UserCourseService extends IService<UserCourse> {

    /**
     * 获取用户的课程列表
     *
     * @param userId 用户ID
     * @param pageParam 分页参数
     * @return 分页结果
     */
    PageResult<UserCourseDTO> getUserCourses(Long userId, PageParam pageParam);

    /**
     * 获取用户课程详情
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 课程详情
     */
    UserCourseDTO getUserCourseDetail(Long userId, Long courseId);

    /**
     * 检查用户是否已购买课程
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否已购买
     */
    boolean checkUserHasCourse(Long userId, Long courseId);

    /**
     * 更新课程学习进度
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param progress 进度(0-100)
     * @return 是否更新成功
     */
    boolean updateCourseProgress(Long userId, Long courseId, Integer progress);

    /**
     * 获取用户课程总数
     * 
     * @param userId 用户ID
     * @return 课程数量
     */
    Integer getUserCourseCount(Long userId);

    /**
     * 根据 MQ 消息开通课程（由 OrderPaidMessageListener 调用）
     * 消费 order-service 发来的支付成功消息，为用户开通订单中的所有课程
     *
     * @param message 订单支付成功消息
     */
    void activateCoursesFromMessage(OrderPaidMessage message);
}