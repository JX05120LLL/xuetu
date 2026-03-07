package com.star.learning.controller;

import com.star.common.result.R;
import com.star.learning.dto.LearningRecordDTO;
import com.star.learning.dto.LearningStatsDTO;
import com.star.learning.dto.UpdateLearningProgressRequest;
import com.star.learning.service.LearningRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 学习记录控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/learning/record")
@RequiredArgsConstructor
@Tag(name = "学习记录管理", description = "学习进度跟踪、学习统计等功能")
public class LearningRecordController {

    private final LearningRecordService learningRecordService;

    /**
     * 更新学习进度
     */
    @PostMapping("/progress")
    @Operation(summary = "更新学习进度", description = "记录用户的课时学习进度")
    public R<Boolean> updateLearningProgress(@Valid @RequestBody UpdateLearningProgressRequest request,
                                            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean result = learningRecordService.updateLearningProgress(userId, request);
        return R.ok(result, "学习进度更新成功");
    }

    /**
     * 同步课程进度到user_course表
     * 当用户学习课时时，自动同步进度到user_course表，便于前端快速查询
     */
    @PostMapping("/sync-course-progress/{courseId}")
    @Operation(summary = "同步课程进度", description = "同步用户课程学习进度到user_course表")
    public R<Boolean> syncCourseProgress(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean result = learningRecordService.syncCourseProgress(userId, courseId);
        return R.ok(result, "课程进度同步成功");
    }

    /**
     * 获取课程学习记录
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程学习记录", description = "查询用户某课程的所有学习记录")
    public R<List<LearningRecordDTO>> getCourseRecords(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        List<LearningRecordDTO> records = learningRecordService.getUserCourseRecords(userId, courseId);
        return R.ok(records);
    }

    /**
     * 获取学习统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取学习统计", description = "查询用户的学习统计数据")
    public R<LearningStatsDTO> getLearningStats(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        LearningStatsDTO stats = learningRecordService.getUserLearningStats(userId);
        return R.ok(stats);
    }

    /**
     * 获取最近学习记录
     */
    @GetMapping("/recent")
    @Operation(summary = "获取最近学习记录", description = "查询用户最近的学习记录")
    public R<List<LearningRecordDTO>> getRecentRecords(
            @Parameter(description = "限制条数", example = "10") @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        List<LearningRecordDTO> records = learningRecordService.getRecentLearningRecords(userId, limit);
        return R.ok(records);
    }

    /**
     * 获取课程学习进度
     */
    @GetMapping("/progress/{courseId}")
    @Operation(summary = "获取课程学习进度", description = "查询用户某课程的整体学习进度")
    public R<Double> getCourseProgress(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Double progress = learningRecordService.getCourseProgress(userId, courseId);
        return R.ok(progress != null ? progress : 0.0);
    }

    /**
     * 标记课时完成
     */
    @PostMapping("/complete")
    @Operation(summary = "标记课时完成", description = "标记某个课时为已完成状态")
    public R<Boolean> markLessonCompleted(
            @Parameter(description = "课程ID", required = true) @RequestParam Long courseId,
            @Parameter(description = "课时ID", required = true) @RequestParam Long lessonId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean result = learningRecordService.markLessonCompleted(userId, courseId, lessonId);
        return R.ok(result, "课时完成状态更新成功");
    }

    /**
     * 检查是否已学习
     */
    @GetMapping("/check/{lessonId}")
    @Operation(summary = "检查学习状态", description = "检查用户是否已学习某个课时")
    public R<Boolean> checkLearningStatus(
            @Parameter(description = "课时ID", required = true) @PathVariable Long lessonId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean hasLearned = learningRecordService.hasLearned(userId, lessonId);
        return R.ok(hasLearned);
    }

    /**
     * 获取学习排名
     */
    @GetMapping("/rank")
    @Operation(summary = "获取学习排名", description = "查询用户的学习排名")
    public R<Integer> getLearningRank(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Integer rank = learningRecordService.getUserLearningRank(userId);
        return R.ok(rank);
    }

    /**
     * 从请求中获取用户ID
     * TODO: 实际项目中应该从JWT Token中解析
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 临时方案：从请求头中获取用户ID
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr != null) {
            try {
                return Long.valueOf(userIdStr);
            } catch (NumberFormatException e) {
                log.warn("用户ID格式错误: {}", userIdStr);
            }
        }
        
        // 默认返回测试用户ID
        return 1L;
    }
}