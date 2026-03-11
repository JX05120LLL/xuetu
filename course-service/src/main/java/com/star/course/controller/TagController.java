package com.star.course.controller;

import com.star.common.result.R;
import com.star.course.dto.CourseTagRequest;
import com.star.course.dto.TagDTO;
import com.star.course.dto.TagRequest;
import com.star.course.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 标签管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "标签管理", description = "课程标签相关接口")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "查询所有标签", description = "获取系统中所有的标签列表")
    public R<List<TagDTO>> getAllTags() {
        List<TagDTO> tags = tagService.getAllTags();
        return R.ok(tags);
    }

    @GetMapping("/popular")
    @Operation(summary = "查询热门标签", description = "根据使用次数获取热门标签列表")
    public R<List<TagDTO>> getPopularTags(
            @Parameter(description = "数量限制，默认10个") 
            @RequestParam(name = "limit", defaultValue = "10") Integer limit) {
        List<TagDTO> tags = tagService.getPopularTags(limit);
        return R.ok(tags);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "查询课程标签", description = "根据课程ID获取该课程的所有标签")
    public R<List<TagDTO>> getCourseTag(
            @Parameter(description = "课程ID", required = true) 
            @PathVariable Long courseId) {
        List<TagDTO> tags = tagService.getTagsByCourseId(courseId);
        return R.ok(tags);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询标签详情", description = "根据ID获取标签详细信息")
    public R<TagDTO> getTagDetail(
            @Parameter(description = "标签ID", required = true) 
            @PathVariable Long id) {
        TagDTO tag = tagService.getTagDetail(id);
        return R.ok(tag);
    }

    @PostMapping
    @Operation(summary = "创建标签", description = "创建新的课程标签")
    public R<Long> createTag(@Valid @RequestBody TagRequest request) {
        Long tagId = tagService.createTag(request);
        return R.ok(tagId, "标签创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新标签", description = "更新指定ID的标签信息")
    public R<Boolean> updateTag(
            @Parameter(description = "标签ID", required = true) 
            @PathVariable Long id,
            @Valid @RequestBody TagRequest request) {
        Boolean result = tagService.updateTag(id, request);
        return R.ok(result, "标签更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签", description = "删除指定ID的标签（仅当无课程使用时）")
    public R<Boolean> deleteTag(
            @Parameter(description = "标签ID", required = true) 
            @PathVariable Long id) {
        Boolean result = tagService.deleteTag(id);
        return R.ok(result, "标签删除成功");
    }

    @PostMapping("/course/set")
    @Operation(summary = "设置课程标签", description = "为指定课程设置标签列表（会覆盖原有标签）")
    public R<Boolean> setCourseTag(@Valid @RequestBody CourseTagRequest request) {
        Boolean result = tagService.setCourseTag(request);
        return R.ok(result, "课程标签设置成功");
    }

    @PostMapping("/course/{courseId}/tag/{tagId}")
    @Operation(summary = "添加课程标签", description = "为指定课程添加单个标签")
    public R<Boolean> addCourseTag(
            @Parameter(description = "课程ID", required = true) 
            @PathVariable Long courseId,
            @Parameter(description = "标签ID", required = true) 
            @PathVariable Long tagId) {
        Boolean result = tagService.addCourseTag(courseId, tagId);
        return R.ok(result, "课程标签添加成功");
    }

    @DeleteMapping("/course/{courseId}/tag/{tagId}")
    @Operation(summary = "移除课程标签", description = "移除指定课程的指定标签")
    public R<Boolean> removeCourseTag(
            @Parameter(description = "课程ID", required = true) 
            @PathVariable Long courseId,
            @Parameter(description = "标签ID", required = true) 
            @PathVariable Long tagId) {
        Boolean result = tagService.removeCourseTag(courseId, tagId);
        return R.ok(result, "课程标签移除成功");
    }

    @DeleteMapping("/course/{courseId}/tags")
    @Operation(summary = "清除课程所有标签", description = "移除指定课程的所有标签关联")
    public R<Boolean> clearCourseTags(
            @Parameter(description = "课程ID", required = true) 
            @PathVariable Long courseId) {
        Boolean result = tagService.clearCourseTags(courseId);
        return R.ok(result, "课程标签清除成功");
    }

    @GetMapping("/{id}/usage")
    @Operation(summary = "查询标签使用次数", description = "获取指定标签被多少个课程使用")
    public R<Integer> getTagUsage(
            @Parameter(description = "标签ID", required = true) 
            @PathVariable Long id) {
        Integer usageCount = tagService.countUsage(id);
        return R.ok(usageCount);
    }
}