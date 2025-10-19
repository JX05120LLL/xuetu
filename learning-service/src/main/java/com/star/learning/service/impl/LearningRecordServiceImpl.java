package com.star.learning.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.learning.dto.LearningRecordDTO;
import com.star.learning.dto.LearningStatsDTO;
import com.star.learning.dto.UpdateLearningProgressRequest;
import com.star.learning.entity.LearningRecord;
import com.star.learning.mapper.LearningRecordMapper;
import com.star.learning.mapper.NoteMapper;
import com.star.learning.mapper.UserCourseMapper;
import com.star.learning.service.LearningRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学习记录服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> 
        implements LearningRecordService {

    private final LearningRecordMapper learningRecordMapper;
    private final UserCourseMapper userCourseMapper;
    private final NoteMapper noteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLearningProgress(Long userId, UpdateLearningProgressRequest request) {
        log.info("更新学习进度: userId={}, courseId={}, lessonId={}, progress={}", 
                userId, request.getCourseId(), request.getLessonId(), request.getProgress());

        LocalDateTime now = LocalDateTime.now();
        
        // 直接插入或更新学习记录（使用 ON DUPLICATE KEY UPDATE）
        LearningRecord record = new LearningRecord();
        record.setUserId(userId);
        record.setCourseId(request.getCourseId());
        record.setLessonId(request.getLessonId());
        record.setProgress(request.getProgress());
        record.setIsCompleted(request.getIsCompleted() != null ? request.getIsCompleted() : 0);
        record.setLastLearnTime(now);
        record.setCreatedTime(now);
        record.setUpdatedTime(now);
        
        int result = learningRecordMapper.insertLearningRecord(record);
        if (result <= 0) {
            log.error("更新学习记录失败");
            return false;
        }

        // 更新用户课程总体进度
        updateCourseOverallProgress(userId, request.getCourseId());

        log.info("学习进度更新成功: userId={}, courseId={}, lessonId={}", 
                userId, request.getCourseId(), request.getLessonId());
        return true;
    }

    @Override
    public List<LearningRecordDTO> getUserCourseRecords(Long userId, Long courseId) {
        log.info("查询用户课程学习记录: userId={}, courseId={}", userId, courseId);

        List<LearningRecord> records = learningRecordMapper.selectByUserIdAndCourseId(userId, courseId);
        return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LearningStatsDTO getUserLearningStats(Long userId) {
        log.info("查询用户学习统计: userId={}", userId);

        LearningStatsDTO stats = new LearningStatsDTO();
        stats.setUserId(userId);

        // 查询总学习时长（转换为分钟）
        Long totalLearningTimeSeconds = learningRecordMapper.selectTotalLearningTimeByUserId(userId);
        stats.setTotalLearningTime(totalLearningTimeSeconds != null ? totalLearningTimeSeconds.intValue() / 60 : 0);

        // 查询今日学习时长（转换为分钟）
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.now().with(LocalTime.MAX);
        Long todayLearningTimeSeconds = learningRecordMapper.selectLearningTimeByUserIdAndDateRange(
                userId, todayStart, todayEnd);
        stats.setTodayLearningTime(todayLearningTimeSeconds != null ? todayLearningTimeSeconds.intValue() / 60 : 0);

        // 查询本周学习时长（转换为分钟）
        LocalDateTime weekStart = LocalDateTime.now().minus(7, ChronoUnit.DAYS).with(LocalTime.MIN);
        Long weekLearningTimeSeconds = learningRecordMapper.selectLearningTimeByUserIdAndDateRange(
                userId, weekStart, LocalDateTime.now());
        stats.setWeekLearningTime(weekLearningTimeSeconds != null ? weekLearningTimeSeconds.intValue() / 60 : 0);

        // 查询本月学习时长（转换为分钟）
        LocalDateTime monthStart = LocalDateTime.now().minus(30, ChronoUnit.DAYS).with(LocalTime.MIN);
        Long monthLearningTimeSeconds = learningRecordMapper.selectLearningTimeByUserIdAndDateRange(
                userId, monthStart, LocalDateTime.now());
        stats.setMonthLearningTime(monthLearningTimeSeconds != null ? monthLearningTimeSeconds.intValue() / 60 : 0);

        // 查询课程统计（从user_course表）
        Integer totalCourses = userCourseMapper.countTotalCoursesByUserId(userId);
        stats.setTotalCourses(totalCourses != null ? totalCourses : 0);

        Integer completedCourses = userCourseMapper.countCompletedCoursesByUserId(userId);
        stats.setCompletedCourses(completedCourses != null ? completedCourses : 0);

        // 正在学习的课程数 = 总课程数 - 完成的课程数
        stats.setLearningCourses(stats.getTotalCourses() - stats.getCompletedCourses());

        // 查询完成的课时数
        Integer completedLessons = learningRecordMapper.countCompletedLessonsByUserId(userId);
        stats.setCompletedLessons(completedLessons != null ? completedLessons : 0);

        // 查询笔记数
        Integer totalNotes = noteMapper.countByUserId(userId);
        stats.setTotalNotes(totalNotes != null ? totalNotes : 0);

        // 计算连续学习天数
        stats.setContinuousDays(getConsecutiveLearningDays(userId));

        // 查询最后学习时间
        List<LearningRecord> recentRecords = learningRecordMapper.selectRecentLearningRecords(userId, 1);
        if (!recentRecords.isEmpty()) {
            stats.setLastLearnTime(recentRecords.get(0).getLastLearnTime());
        }

        // 计算平均进度（从user_course表）
        Double avgProgress = userCourseMapper.selectAverageProgressByUserId(userId);
        stats.setAverageProgress(avgProgress != null ? avgProgress.intValue() : 0);

        return stats;
    }

    @Override
    public List<LearningRecordDTO> getRecentLearningRecords(Long userId, Integer limit) {
        log.info("查询最近学习记录: userId={}, limit={}", userId, limit);

        List<LearningRecord> records = learningRecordMapper.selectRecentLearningRecords(userId, limit);
        return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double getCourseProgress(Long userId, Long courseId) {
        return learningRecordMapper.selectCourseProgressByUserIdAndCourseId(userId, courseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean markLessonCompleted(Long userId, Long courseId, Long lessonId) {
        log.info("标记课时完成: userId={}, courseId={}, lessonId={}", userId, courseId, lessonId);

        LocalDateTime now = LocalDateTime.now();
        
        // 使用专门的标记完成方法，会自动保持原有进度
        int result = learningRecordMapper.markLessonCompleted(userId, courseId, lessonId, now, now);
        if (result > 0) {
            // 更新用户课程总体进度
            updateCourseOverallProgress(userId, courseId);
            log.info("标记课时完成成功: userId={}, courseId={}, lessonId={}", userId, courseId, lessonId);
            return true;
        }
        
        log.error("标记课时完成失败: userId={}, courseId={}, lessonId={}", userId, courseId, lessonId);
        return false;
    }

    @Override
    public Boolean hasLearned(Long userId, Long lessonId) {
        LearningRecord record = learningRecordMapper.selectByUserIdAndCourseIdAndLessonId(
                userId, null, lessonId);
        return record != null && record.getProgress() > 0;
    }

    @Override
    public Integer getUserLearningRank(Long userId) {
        // TODO: 实现用户学习排名逻辑
        return 1;
    }

    @Override
    public Integer getConsecutiveLearningDays(Long userId) {
        // TODO: 实现连续学习天数计算逻辑
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchUpdateProgress(List<LearningRecord> records) {
        if (records == null || records.isEmpty()) {
            return true;
        }

        int updated = learningRecordMapper.batchUpdateProgress(records);
        return updated > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncCourseProgress(Long userId, Long courseId) {
        log.info("同步课程进度: userId={}, courseId={}", userId, courseId);
        
        try {
            // 使用新的计算方法：只要有学习记录就算已学（支持快进、跳看等场景）
            Double actualProgress = learningRecordMapper.calculateActualCourseProgress(userId, courseId);
            
            if (actualProgress != null) {
                int progressInt = actualProgress.intValue();
                log.info("计算出的实际进度: {}%", progressInt);
                
                // 更新user_course表的progress字段
                int updated = userCourseMapper.updateCourseProgress(userId, courseId, progressInt);
                
                if (updated > 0) {
                    log.info("课程进度同步成功: userId={}, courseId={}, progress={}%", 
                            userId, courseId, progressInt);
                    return true;
                } else {
                    log.warn("未找到对应的user_course记录: userId={}, courseId={}", userId, courseId);
                    return false;
                }
            } else {
                log.warn("无法计算课程进度: userId={}, courseId={}", userId, courseId);
                return false;
            }
        } catch (Exception e) {
            log.error("同步课程进度失败: userId={}, courseId={}", userId, courseId, e);
            throw e;
        }
    }

    /**
     * 更新课程总体进度
     */
    private void updateCourseOverallProgress(Long userId, Long courseId) {
        // 使用新的计算方法（只要有学习记录就算已学）
        Double progress = learningRecordMapper.calculateActualCourseProgress(userId, courseId);
        if (progress != null) {
            userCourseMapper.updateCourseProgress(userId, courseId, progress.intValue());
            log.info("自动同步课程进度: userId={}, courseId={}, progress={}%", 
                    userId, courseId, progress.intValue());
        }
    }

    /**
     * 转换为DTO
     */
    private LearningRecordDTO convertToDTO(LearningRecord record) {
        LearningRecordDTO dto = new LearningRecordDTO();
        BeanUtils.copyProperties(record, dto);
        dto.setCreateTime(record.getCreatedTime());
        dto.setUpdateTime(record.getUpdatedTime());

        // 设置完成状态名称
        if (record.getIsCompleted() != null) {
            for (LearningRecord.CompletionStatus status : LearningRecord.CompletionStatus.values()) {
                if (status.getValue().equals(record.getIsCompleted())) {
                    dto.setCompletionStatusName(status.getLabel());
                    break;
                }
            }
        }

        return dto;
    }
}