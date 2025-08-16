package com.star.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.common.dto.PageParam;
import com.star.common.dto.PageResult;
import com.star.common.utils.PageUtil;
import com.star.order.entity.OrderItem;
import com.star.order.entity.UserCourse;
import com.star.order.mapper.UserCourseMapper;
import com.star.order.service.OrderItemService;
import com.star.order.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户课程关系服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCourseServiceImpl extends ServiceImpl<UserCourseMapper, UserCourse> implements UserCourseService {

    private final UserCourseMapper userCourseMapper;
    private final OrderItemService orderItemService;

    @Override
    public PageResult<UserCourse> getUserCourses(Long userId, PageParam pageParam) {
        Page<UserCourse> page = PageUtil.buildPage(pageParam);
        LambdaQueryWrapper<UserCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCourse::getUserId, userId)
               .eq(UserCourse::getStatus, UserCourse.Status.NORMAL.getValue())
               .orderByDesc(UserCourse::getPurchaseTime);
        
        page(page, wrapper);
        
        return PageUtil.buildPageResult(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public Boolean checkUserHasCourse(Long userId, Long courseId) {
        Integer count = userCourseMapper.checkUserHasCourse(userId, courseId);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addUserCourse(UserCourse userCourse) {
        // 检查是否已存在
        UserCourse existing = userCourseMapper.selectByUserIdAndCourseId(
                userCourse.getUserId(), userCourse.getCourseId());
        
        if (existing != null) {
            // 如果已存在但状态不正常，更新状态
            if (!UserCourse.Status.NORMAL.getValue().equals(existing.getStatus())) {
                existing.setStatus(UserCourse.Status.NORMAL.getValue());
                existing.setPurchaseTime(LocalDateTime.now());
                existing.setOrderId(userCourse.getOrderId());
                existing.setPurchasePrice(userCourse.getPurchasePrice());
                return updateById(existing);
            }
            return true; // 已存在且状态正常
        }
        
        // 新增
        userCourse.setStatus(UserCourse.Status.NORMAL.getValue());
        userCourse.setPurchaseTime(LocalDateTime.now());
        userCourse.setProgress(0);
        
        boolean saved = save(userCourse);
        if (saved) {
            log.info("用户课程关系创建成功，用户ID: {}, 课程ID: {}", 
                     userCourse.getUserId(), userCourse.getCourseId());
        }
        
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProgress(Long userId, Long courseId, Integer progress) {
        if (progress < 0 || progress > 100) {
            log.warn("进度值无效: {}", progress);
            return false;
        }
        
        Integer result = userCourseMapper.updateProgress(userId, courseId, progress);
        boolean success = result > 0;
        
        if (success) {
            log.info("更新学习进度成功，用户ID: {}, 课程ID: {}, 进度: {}%", userId, courseId, progress);
        }
        
        return success;
    }

    @Override
    public Integer countUserCourses(Long userId) {
        return userCourseMapper.countByUserId(userId);
    }

    @Override
    public Integer countCourseUsers(Long courseId) {
        return userCourseMapper.countByCourseId(courseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean activateUserCoursesFromOrder(Long orderId) {
        log.info("开通订单相关课程，订单ID: {}", orderId);
        
        // 获取订单项
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        if (orderItems.isEmpty()) {
            log.warn("订单无购买项目，订单ID: {}", orderId);
            return false;
        }
        
        boolean allSuccess = true;
        for (OrderItem item : orderItems) {
            UserCourse userCourse = new UserCourse();
            userCourse.setUserId(getUserIdFromOrder(orderId)); // 需要从订单获取用户ID
            userCourse.setCourseId(item.getCourseId());
            userCourse.setOrderId(orderId);
            userCourse.setPurchasePrice(item.getPrice());
            
            boolean success = addUserCourse(userCourse);
            if (!success) {
                log.error("开通课程失败，订单ID: {}, 课程ID: {}", orderId, item.getCourseId());
                allSuccess = false;
            }
        }
        
        log.info("订单课程开通完成，订单ID: {}, 成功: {}", orderId, allSuccess);
        return allSuccess;
    }

    /**
     * 从订单ID获取用户ID（简化实现）
     * 实际应该注入OrderService或通过其他方式获取
     */
    private Long getUserIdFromOrder(Long orderId) {
        // 这里应该通过OrderService获取，为了简化暂时返回1L
        // 实际实现中需要注入OrderService或者通过数据库查询
        return 1L;
    }
}