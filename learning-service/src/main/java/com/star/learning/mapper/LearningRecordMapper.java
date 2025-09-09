package com.star.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.learning.entity.LearningRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习记录Mapper
 * 
 * @author star
 */
@Mapper
public interface LearningRecordMapper extends BaseMapper<LearningRecord> {

    /**
     * 根据用户ID和课程ID查询学习记录
     */
    List<LearningRecord> selectByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 根据用户ID、课程ID和课时ID查询学习记录
     */
    LearningRecord selectByUserIdAndCourseIdAndLessonId(@Param("userId") Long userId, 
                                                       @Param("courseId") Long courseId, 
                                                       @Param("lessonId") Long lessonId);

    /**
     * 查询用户总学习时长
     */
    Long selectTotalLearningTimeByUserId(@Param("userId") Long userId);

    /**
     * 查询用户指定时间段学习时长
     */
    Long selectLearningTimeByUserIdAndDateRange(@Param("userId") Long userId, 
                                               @Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 查询用户学习的课程数量
     */
    Integer countLearningCoursesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户已完成的课程数量
     */
    Integer countCompletedCoursesByUserId(@Param("userId") Long userId);

    /**
     * 查询最近学习记录
     */
    List<LearningRecord> selectRecentLearningRecords(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询课程学习进度
     */
    Double selectCourseProgressByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 批量更新学习进度
     */
    int batchUpdateProgress(@Param("records") List<LearningRecord> records);

    /**
     * 插入学习记录
     */
    int insertLearningRecord(LearningRecord record);

    /**
     * 根据复合主键更新学习记录
     */
    int updateLearningRecord(LearningRecord record);

    /**
     * 标记课时完成（保持原有进度）
     */
    int markLessonCompleted(@Param("userId") Long userId, 
                           @Param("courseId") Long courseId, 
                           @Param("lessonId") Long lessonId,
                           @Param("lastLearnTime") LocalDateTime lastLearnTime,
                           @Param("updatedTime") LocalDateTime updatedTime);
}