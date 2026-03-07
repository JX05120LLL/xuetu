package com.star.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.star.common.dto.PageParam;
import com.star.common.result.R;
import com.star.course.dto.*;
import com.star.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 课程管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Tag(name = "课程管理", description = "课程的增删改查、发布、下架等功能")
public class CourseController {

    private final CourseService courseService;

    /**
     * 分页查询课程列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询课程列表", description = "支持按标题、分类、状态、难度级别筛选")
    public R<IPage<CourseDTO>> getCoursePage(
            @Valid PageParam pageParam,
            @Parameter(description = "课程标题（模糊查询）") @RequestParam(required = false) String title,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "状态(0:未发布,1:已发布,2:已下架)") @RequestParam(required = false) Integer status,
            @Parameter(description = "难度级别(0:初级,1:中级,2:高级)") @RequestParam(required = false) Integer level) {
        
        IPage<CourseDTO> result = courseService.getCoursePage(pageParam, title, categoryId, status, level);
        return R.ok(result);
    }

    /**
     * 查询课程详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询课程详情", description = "根据课程ID查询详细信息")
    public R<CourseDTO> getCourseDetail(@Parameter(description = "课程ID") @PathVariable Long id) {
        CourseDTO result = courseService.getCourseDetail(id);
        return R.ok(result);
    }

    /**
     * 创建课程
     */
    @PostMapping
    @Operation(summary = "创建课程", description = "创建新的课程")
    public R<Long> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        Long courseId = courseService.createCourse(request);
        return R.ok(courseId);
    }

    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新课程", description = "更新课程信息")
    public R<Boolean> updateCourse(
            @Parameter(description = "课程ID") @PathVariable Long id,
            @Valid @RequestBody CourseUpdateRequest request) {
        Boolean result = courseService.updateCourse(id, request);
        return R.ok(result);
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程", description = "删除课程（仅限未发布的课程）")
    public R<Boolean> deleteCourse(@Parameter(description = "课程ID") @PathVariable Long id) {
        Boolean result = courseService.deleteCourse(id);
        return R.ok(result);
    }

    /**
     * 发布课程
     */
    @PostMapping("/{id}/publish")
    @Operation(summary = "发布课程", description = "发布课程，使其对外可见")
    public R<Boolean> publishCourse(@Parameter(description = "课程ID") @PathVariable Long id) {
        Boolean result = courseService.publishCourse(id);
        return R.ok(result);
    }

    /**
     * 下架课程
     */
    @PostMapping("/{id}/offline")
    @Operation(summary = "下架课程", description = "下架课程，停止对外展示")
    public R<Boolean> offlineCourse(@Parameter(description = "课程ID") @PathVariable Long id) {
        Boolean result = courseService.offlineCourse(id);
        return R.ok(result);
    }

    /**
     * 查询热门课程
     */
    @GetMapping("/hot")
    @Operation(summary = "查询热门课程", description = "根据学习人数查询热门课程")
    public R<List<CourseDTO>> getHotCourses(
            @Parameter(description = "数量限制，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        List<CourseDTO> result = courseService.getHotCourses(limit);
        return R.ok(result);
    }

    /**
     * 搜索课程
     */
    @GetMapping("/search")
    @Operation(summary = "搜索课程", description = "根据关键词搜索课程")
    public R<IPage<CourseDTO>> searchCourses(
            @Valid PageParam pageParam,
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        IPage<CourseDTO> result = courseService.searchCourses(pageParam, keyword);
        return R.ok(result);
    }

    /**
     * 增加学习人数
     */
    @PostMapping("/{id}/student-count")
    @Operation(summary = "增加学习人数", description = "用户购买课程后调用此接口")
    public R<Boolean> increaseStudentCount(
            @Parameter(description = "课程ID") @PathVariable Long id,
            @Parameter(description = "增量，默认1") @RequestParam(defaultValue = "1") Integer increment) {
        Boolean result = courseService.increaseStudentCount(id, increment);
        return R.ok(result);
    }

    /**
     * 批量更新课程状态
     */
    @PostMapping("/batch-status")
    @Operation(summary = "批量更新课程状态", description = "批量发布或下架课程")
    public R<Boolean> batchUpdateStatus(
            @Parameter(description = "课程ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "新状态") @RequestParam Integer status) {
        Boolean result = courseService.batchUpdateStatus(ids, status);
        return R.ok(result);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查课程服务是否正常")
    public R<String> health() {
        return R.ok("课程服务运行正常");
    }
}