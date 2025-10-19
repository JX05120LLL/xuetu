package com.star.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.learning.entity.LearningRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习记录Mapper接口
 *
 * @author star
 */
@Mapper
@Repository
public interface LearningRecordMapper extends BaseMapper<LearningRecord> {

    /**
     * 插入或更新学习记录
     *
     * @param record 学习记录
     * @return 影响的行数
     */
    @Insert("INSERT INTO learning_record (user_id, course_id, lesson_id, progress, last_position, is_completed, last_learn_time, created_time, updated_time) " +
            "VALUES(#{userId}, #{courseId}, #{lessonId}, #{progress}, #{lastPosition}, #{isCompleted}, #{lastLearnTime}, #{createdTime}, #{updatedTime}) " +
            "ON DUPLICATE KEY UPDATE progress = #{progress}, last_position = #{lastPosition}, is_completed = #{isCompleted}, " +
            "last_learn_time = #{lastLearnTime}, updated_time = #{updatedTime}")
    int insertLearningRecord(LearningRecord record);

    /**
     * 根据用户ID和课程ID查询学习记录
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 学习记录列表
     */
    @Select("SELECT * FROM learning_record WHERE user_id = #{userId} AND course_id = #{courseId}")
    List<LearningRecord> selectByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 查询用户总学习时长
     *
     * @param userId 用户ID
     * @return 总学习时长（秒）
     */
    @Select("SELECT COALESCE(SUM(progress), 0) FROM learning_record WHERE user_id = #{userId}")
    Long selectTotalLearningTimeByUserId(@Param("userId") Long userId);

    /**
     * 查询用户在指定时间范围内的学习时长
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 学习时长（秒）
     */
    @Select("SELECT COALESCE(SUM(progress), 0) FROM learning_record WHERE user_id = #{userId} " +
            "AND last_learn_time BETWEEN #{startTime} AND #{endTime}")
    Long selectLearningTimeByUserIdAndDateRange(@Param("userId") Long userId, 
                                              @Param("startTime") LocalDateTime startTime, 
                                              @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户学习的课程数量
     *
     * @param userId 用户ID
     * @return 课程数量
     */
    @Select("SELECT COUNT(DISTINCT course_id) FROM learning_record WHERE user_id = #{userId}")
    Integer countLearningCoursesByUserId(@Param("userId") Long userId);

    /**
     * 统计用户已完成的课程数量
     *
     * @param userId 用户ID
     * @return 已完成课程数量
     */
    @Select("SELECT COUNT(1) FROM (SELECT course_id FROM learning_record WHERE user_id = #{userId} " +
            "GROUP BY course_id HAVING AVG(is_completed) >= 1) AS completed_courses")
    Integer countCompletedCoursesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户最近的学习记录
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 最近学习记录列表
     */
    @Select("SELECT * FROM learning_record WHERE user_id = #{userId} ORDER BY last_learn_time DESC LIMIT #{limit}")
    List<LearningRecord> selectRecentLearningRecords(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询用户对某课程的学习进度
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 课程进度（百分比，0-100）
     */
    @Select("SELECT ROUND(AVG(progress), 2) FROM learning_record WHERE user_id = #{userId} AND course_id = #{courseId}")
    Double selectCourseProgressByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 标记课时为已完成
     *
     * @param userId      用户ID
     * @param courseId    课程ID
     * @param lessonId    课时ID
     * @param lastLearnTime 最后学习时间
     * @param updatedTime   更新时间
     * @return 影响的行数
     */
    @Update("UPDATE learning_record SET is_completed = 1, last_learn_time = #{lastLearnTime}, " +
            "updated_time = #{updatedTime} WHERE user_id = #{userId} AND course_id = #{courseId} " +
            "AND lesson_id = #{lessonId}")
    int markLessonCompleted(@Param("userId") Long userId, 
                          @Param("courseId") Long courseId, 
                          @Param("lessonId") Long lessonId, 
                          @Param("lastLearnTime") LocalDateTime lastLearnTime, 
                          @Param("updatedTime") LocalDateTime updatedTime);

    /**
     * 根据用户ID、课程ID和课时ID查询学习记录
     *
     * @param userId   用户ID
     * @param courseId 课程ID（可为null）
     * @param lessonId 课时ID
     * @return 学习记录
     */
    @Select("<script>" +
            "SELECT * FROM learning_record WHERE user_id = #{userId} AND lesson_id = #{lessonId}" +
            "<if test='courseId != null'> AND course_id = #{courseId}</if> LIMIT 1" +
            "</script>")
    LearningRecord selectByUserIdAndCourseIdAndLessonId(@Param("userId") Long userId, 
                                                     @Param("courseId") Long courseId, 
                                                     @Param("lessonId") Long lessonId);

    /**
     * 批量更新学习进度
     *
     * @param records 学习记录列表
     * @return 影响的行数
     */
    default int batchUpdateProgress(List<LearningRecord> records) {
        if (records == null || records.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (LearningRecord record : records) {
            // 使用已有的insertLearningRecord方法实现批量更新
            count += insertLearningRecord(record);
        }
        return count;
    }

    /**
     * 计算用户在某课程的总学习时长（秒）
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 总学习时长（秒）
     */
    @Select("SELECT COALESCE(SUM(progress), 0) FROM learning_record WHERE user_id = #{userId} AND course_id = #{courseId}")
    Integer sumStudyDuration(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    /**
     * 统计用户已完成的课时数量
     *
     * @param userId 用户ID
     * @return 已完成课时数量
     */
    @Select("SELECT COUNT(*) FROM learning_record WHERE user_id = #{userId} AND is_completed = 1")
    Integer countCompletedLessonsByUserId(@Param("userId") Long userId);

    /**
     * 计算课程进度（只要有学习记录就算已学，支持快进、跳看等场景）
     * 计算公式：已学课时数 / 总课时数 * 100
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 课程进度（百分比，0-100）
     */
    Double calculateActualCourseProgress(@Param("userId") Long userId, @Param("courseId") Long courseId);
}