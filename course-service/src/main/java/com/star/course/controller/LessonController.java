package com.star.course.controller;

import com.star.common.result.R;
import com.star.course.dto.LessonDTO;
import com.star.course.dto.LessonRequest;
import com.star.course.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 课时管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/lesson")
@RequiredArgsConstructor
@Validated
@Tag(name = "课时管理", description = "课时管理相关接口")
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/chapter/{chapterId}")
    @Operation(summary = "查询章节课时列表", description = "根据章节ID查询该章节下的所有课时")
    public R<List<LessonDTO>> getLessonsByChapterId(
            @Parameter(description = "章节ID") @PathVariable Long chapterId) {
        List<LessonDTO> lessons = lessonService.getLessonsByChapterId(chapterId);
        return R.ok(lessons);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询课时详情", description = "根据课时ID查询课时详细信息")
    public R<LessonDTO> getLessonDetail(
            @Parameter(description = "课时ID") @PathVariable Long id) {
        LessonDTO lesson = lessonService.getLessonDetail(id);
        return R.ok(lesson);
    }

    @PostMapping("/chapter/{chapterId}")
    @Operation(summary = "创建课时", description = "在指定章节下创建新课时")
    public R<Long> createLesson(
            @Parameter(description = "章节ID") @PathVariable Long chapterId,
            @RequestBody @Valid LessonRequest request) {
        Long lessonId = lessonService.createLesson(chapterId, request);
        return R.ok(lessonId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新课时", description = "更新指定课时的信息")
    public R<Boolean> updateLesson(
            @Parameter(description = "课时ID") @PathVariable Long id,
            @RequestBody @Valid LessonRequest request) {
        Boolean result = lessonService.updateLesson(id, request);
        return R.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课时", description = "删除指定的课时")
    public R<Boolean> deleteLesson(
            @Parameter(description = "课时ID") @PathVariable Long id) {
        Boolean result = lessonService.deleteLesson(id);
        return R.ok(result);
    }

    @PutMapping("/chapter/{chapterId}/sort")
    @Operation(summary = "批量更新课时排序", description = "批量更新章节下课时的排序")
    public R<Boolean> updateLessonSort(
            @Parameter(description = "章节ID") @PathVariable Long chapterId,
            @RequestBody @Parameter(description = "课时ID列表（按新的排序）") List<Long> lessonIds) {
        Boolean result = lessonService.updateLessonSort(chapterId, lessonIds);
        return R.ok(result);
    }

    @GetMapping("/course/{courseId}/duration")
    @Operation(summary = "计算课程总时长", description = "计算指定课程的总时长")
    public R<Integer> calculateCourseDuration(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        Integer duration = lessonService.calculateCourseDuration(courseId);
        return R.ok(duration);
    }
}