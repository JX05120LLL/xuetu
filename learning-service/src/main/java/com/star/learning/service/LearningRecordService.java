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
}