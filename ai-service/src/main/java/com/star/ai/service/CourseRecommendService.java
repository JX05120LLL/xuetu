package com.star.ai.service;

import com.star.ai.dto.CourseRecommendation;
import com.star.ai.dto.LearningPath;

import java.util.List;

/**
 * 课程推荐服务接口
 * 
 * @author star
 */
public interface CourseRecommendService {

    /**
     * 个性化课程推荐
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐课程列表
     */
    List<CourseRecommendation> recommendCourses(Long userId, Integer limit);

    /**
     * 智能学习路径推荐
     * 
     * @param userId 用户ID
     * @param goal 学习目标
     * @return 学习路径
     */
    LearningPath recommendLearningPath(Long userId, String goal);
}
