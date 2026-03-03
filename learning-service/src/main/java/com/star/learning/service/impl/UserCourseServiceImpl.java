package com.star.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.common.dto.PageParam;
import com.star.common.dto.PageResult;
import com.star.common.mq.OrderPaidMessage;
import com.star.common.result.R;
import com.star.common.utils.PageUtil;
import com.star.learning.dto.UserCourseDTO;
import com.star.learning.entity.UserCourse;
import com.star.learning.feign.CourseServiceClient;
import com.star.learning.mapper.LearningRecordMapper;
import com.star.learning.mapper.UserCourseMapper;
import com.star.learning.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户课程服务实现类
 *
 * @author star
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserCourseServiceImpl extends ServiceImpl<UserCourseMapper, UserCourse> implements UserCourseService {

    private final UserCourseMapper userCourseMapper;
    private final LearningRecordMapper learningRecordMapper;
    private final CourseServiceClient courseServiceClient;

    @Override
    public PageResult<UserCourseDTO> getUserCourses(Long userId, PageParam pageParam) {
        log.info("获取用户课程列表，用户ID: {}", userId);

        // 1. 分页查询用户课程
        Page<UserCourse> page = PageUtil.buildPage(pageParam);
        LambdaQueryWrapper<UserCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCourse::getUserId, userId)
                   .eq(UserCourse::getStatus, UserCourse.Status.ACTIVE.getValue())
                   .orderByDesc(UserCourse::getLastLearnTime, UserCourse::getPurchaseTime);
        
        page(page, queryWrapper);
        List<UserCourse> userCourses = page.getRecords();
        
        if (userCourses.isEmpty()) {
            return PageUtil.buildPageResult(new ArrayList<>(), 0L, Long.valueOf(pageParam.getCurrent()), Long.valueOf(pageParam.getSize()));
        }
        
        // 2. 转换为DTO并填充详细信息
        List<UserCourseDTO> dtoList = new ArrayList<>();
        for (UserCourse userCourse : userCourses) {
            UserCourseDTO dto = convertToDTO(userCourse);
            dtoList.add(dto);
        }
        
        return PageUtil.buildPageResult(dtoList, page.getTotal(), Long.valueOf(page.getCurrent()), Long.valueOf(page.getSize()));
    }

    @Override
    public UserCourseDTO getUserCourseDetail(Long userId, Long courseId) {
        log.info("获取用户课程详情，用户ID: {}, 课程ID: {}", userId, courseId);
        
        // 1. 查询用户课程关系
        LambdaQueryWrapper<UserCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCourse::getUserId, userId)
                   .eq(UserCourse::getCourseId, courseId)
                   .eq(UserCourse::getStatus, UserCourse.Status.ACTIVE.getValue());
        
        UserCourse userCourse = getOne(queryWrapper);
        if (userCourse == null) {
            log.warn("未找到用户课程关系，用户ID: {}, 课程ID: {}", userId, courseId);
            return null;
        }
        
        // 2. 转换为DTO并填充详细信息
        UserCourseDTO dto = convertToDTO(userCourse);
        
        // 3. 查询学习时长
        Integer studyDuration = calculateStudyDuration(userId, courseId);
        dto.setStudyDuration(studyDuration);
        
        return dto;
    }

    @Override
    public boolean checkUserHasCourse(Long userId, Long courseId) {
        LambdaQueryWrapper<UserCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCourse::getUserId, userId)
                   .eq(UserCourse::getCourseId, courseId)
                   .eq(UserCourse::getStatus, UserCourse.Status.ACTIVE.getValue());
        
        return count(queryWrapper) > 0;
    }

    @Override
    public boolean updateCourseProgress(Long userId, Long courseId, Integer progress) {
        if (progress < 0 || progress > 100) {
            log.warn("进度值无效: {}", progress);
            return false;
        }
        
        return userCourseMapper.updateProgress(userId, courseId, progress) > 0;
    }

    @Override
    public Integer getUserCourseCount(Long userId) {
        LambdaQueryWrapper<UserCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCourse::getUserId, userId)
                   .eq(UserCourse::getStatus, UserCourse.Status.ACTIVE.getValue());
        
        long count = count(queryWrapper);
        return (int) count;
    }
    
    /**
     * 将UserCourse转换为UserCourseDTO并填充详细信息
     */
    private UserCourseDTO convertToDTO(UserCourse userCourse) {
        UserCourseDTO dto = new UserCourseDTO();
        BeanUtils.copyProperties(userCourse, dto);
        
        // 设置lastStudyTime（从lastLearnTime映射）
        dto.setLastStudyTime(userCourse.getLastLearnTime());
        dto.setStudyDuration(0); // 默认值，后续可计算
        
        try {
            // 调用课程服务获取课程详情
            log.debug("调用课程服务获取课程详情，课程ID: {}", userCourse.getCourseId());
            R<Object> courseResult = courseServiceClient.getCourseById(userCourse.getCourseId());
            
            log.debug("课程服务返回结果: {}", courseResult);
            
            if (courseResult != null && courseResult.getData() != null) {
                Map<String, Object> courseMap = (Map<String, Object>) courseResult.getData();
                
                log.debug("课程详情Map: {}", courseMap);
                
                // 填充课程信息
                String title = getStringValue(courseMap, "title");
                String coverImage = getStringValue(courseMap, "coverImage");
                
                log.debug("课程名称: {}, 封面图: {}", title, coverImage);
                
                dto.setCourseName(title);
                dto.setCoverImage(coverImage);
                dto.setCourseDescription(getStringValue(courseMap, "description"));
                dto.setTeacherId(getLongValue(courseMap, "teacherId"));
                dto.setTeacherName("讲师"); // 默认值，可以后续从用户服务获取
                dto.setTotalDuration(getIntegerValue(courseMap, "totalDuration"));
            } else {
                log.warn("课程服务返回数据为空，课程ID: {}", userCourse.getCourseId());
                // 设置默认值
                dto.setCourseName("课程名称未知");
                dto.setCoverImage("");
            }
        } catch (Exception e) {
            log.error("获取课程详情失败，课程ID: {}", userCourse.getCourseId(), e);
            // 设置默认值，不中断整个流程
            dto.setCourseName("课程详情加载失败");
            dto.setCoverImage("");
        }
        
        return dto;
    }
    
    /**
     * 计算用户学习时长（秒）
     */
    private Integer calculateStudyDuration(Long userId, Long courseId) {
        try {
            return learningRecordMapper.sumStudyDuration(userId, courseId);
        } catch (Exception e) {
            log.error("计算学习时长失败，用户ID: {}, 课程ID: {}", userId, courseId, e);
            return 0;
        }
    }
    
    // 辅助方法：从Map中安全获取String值
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
    
    // 辅助方法：从Map中安全获取Long值
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    // 辅助方法：从Map中安全获取Integer值
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void activateCoursesFromMessage(OrderPaidMessage message) {
        log.info("开始处理订单支付消息，订单号: {}, 用户ID: {}, 课程数: {}",
                message.getOrderNo(), message.getUserId(), message.getCourseItems().size());

        for (OrderPaidMessage.CourseItem item : message.getCourseItems()) {
            // 幂等检查：已开通过的课程直接跳过，防止消息重复消费
            if (checkUserHasCourse(message.getUserId(), item.getCourseId())) {
                log.info("课程已开通，跳过重复激活，用户ID: {}, 课程ID: {}", message.getUserId(), item.getCourseId());
                continue;
            }

            UserCourse userCourse = new UserCourse();
            userCourse.setUserId(message.getUserId());
            userCourse.setCourseId(item.getCourseId());
            userCourse.setOrderId(message.getOrderId());
            userCourse.setPurchasePrice(item.getPurchasePrice());
            userCourse.setPurchaseTime(java.time.LocalDateTime.now());
            userCourse.setStatus(UserCourse.Status.ACTIVE.getValue());
            userCourse.setProgress(0);

            boolean success = save(userCourse);
            if (success) {
                log.info("课程开通成功，用户ID: {}, 课程ID: {}", message.getUserId(), item.getCourseId());
            } else {
                log.error("课程开通失败，用户ID: {}, 课程ID: {}", message.getUserId(), item.getCourseId());
                throw new RuntimeException("课程开通失败，触发事务回滚，orderId=" + message.getOrderId());
            }
        }

        log.info("订单所有课程开通完成，订单号: {}, 用户ID: {}", message.getOrderNo(), message.getUserId());
    }
}