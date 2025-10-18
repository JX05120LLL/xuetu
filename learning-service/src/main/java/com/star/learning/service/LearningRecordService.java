package com.star.learning.service;

import com.star.learning.dto.LearningRecordDTO;
import com.star.learning.dto.LearningStatsDTO;
import com.star.learning.dto.UpdateLearningProgressRequest;
import com.star.learning.entity.LearningRecord;

import java.util.List;

/**
 * 学习记录服务接口
 * 
 * @author star
 */
public interface LearningRecordService {

    /**
     * 更新学习进度
     */
    Boolean updateLearningProgress(Long userId, UpdateLearningProgressRequest request);

    /**
     * 获取用户课程学习记录
     */
    List<LearningRecordDTO> getUserCourseRecords(Long userId, Long courseId);

    /**
     * 获取用户学习统计
     */
    LearningStatsDTO getUserLearningStats(Long userId);

    /**
     * 获取最近学习记录
     */
    List<LearningRecordDTO> getRecentLearningRecords(Long userId, Integer limit);

    /**
     * 获取课程学习进度
     */
    Double getCourseProgress(Long userId, Long courseId);

    /**
     * 标记课时为已完成
     */
    Boolean markLessonCompleted(Long userId, Long courseId, Long lessonId);

    /**
     * 检查用户是否已学习某课时
     */
    Boolean hasLearned(Long userId, Long lessonId);

    /**
     * 获取用户学习排名
     */
    Integer getUserLearningRank(Long userId);

    /**
     * 计算连续学习天数
     */
    Integer getConsecutiveLearningDays(Long userId);

    /**
     * 批量更新学习进度
     */
    Boolean batchUpdateProgress(List<LearningRecord> records);
    
    /**
     * 同步课程进度到user_course表
     * 根据learning_record计算该课程的总体进度，并同步到user_course表
     * 只要有学习记录就算已学（支持快进、跳看等场景）
     * 
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否同步成功
     */
    Boolean syncCourseProgress(Long userId, Long courseId);
}