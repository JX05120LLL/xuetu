package com.star.course.controller;

import com.star.common.result.R;
import com.star.course.dto.ChapterDTO;
import com.star.course.dto.ChapterRequest;
import com.star.course.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 章节管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/chapter")
@RequiredArgsConstructor
@Tag(name = "章节管理", description = "课程章节的增删改查、排序等功能")
public class ChapterController {

    private final ChapterService chapterService;

    /**
     * 查询课程章节列表
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "查询课程章节列表", description = "根据课程ID查询章节列表")
    public R<List<ChapterDTO>> getChaptersByCourseId(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        List<ChapterDTO> result = chapterService.getChaptersByCourseId(courseId);
        return R.ok(result);
    }

    /**
     * 查询章节详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询章节详情", description = "根据章节ID查询详细信息")
    public R<ChapterDTO> getChapterDetail(@Parameter(description = "章节ID") @PathVariable Long id) {
        ChapterDTO result = chapterService.getChapterDetail(id);
        return R.ok(result);
    }

    /**
     * 创建章节
     */
    @PostMapping("/course/{courseId}")
    @Operation(summary = "创建章节", description = "在指定课程下创建新章节")
    public R<Long> createChapter(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Valid @RequestBody ChapterRequest request) {
        Long chapterId = chapterService.createChapter(courseId, request);
        return R.ok(chapterId);
    }

    /**
     * 更新章节
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新章节", description = "更新章节信息")
    public R<Boolean> updateChapter(
            @Parameter(description = "章节ID") @PathVariable Long id,
            @Valid @RequestBody ChapterRequest request) {
        Boolean result = chapterService.updateChapter(id, request);
        return R.ok(result);
    }

    /**
     * 删除章节
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除章节", description = "删除章节（必须没有课时）")
    public R<Boolean> deleteChapter(@Parameter(description = "章节ID") @PathVariable Long id) {
        Boolean result = chapterService.deleteChapter(id);
        return R.ok(result);
    }

    /**
     * 调整章节排序
     */
    @PostMapping("/course/{courseId}/sort")
    @Operation(summary = "调整章节排序", description = "批量调整章节的排序")
    public R<Boolean> sortChapters(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Parameter(description = "章节ID列表（按新的排序）") @RequestBody List<Long> chapterIds) {
        Boolean result = chapterService.sortChapters(courseId, chapterIds);
        return R.ok(result);
    }

    /**
     * 查询课程章节数量
     */
    @GetMapping("/course/{courseId}/count")
    @Operation(summary = "查询课程章节数量", description = "统计指定课程的章节数量")
    public R<Integer> getChapterCount(@Parameter(description = "课程ID") @PathVariable Long courseId) {
        Integer count = chapterService.getChapterCountByCourseId(courseId);
        return R.ok(count);
    }
}