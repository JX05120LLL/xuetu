package com.star.learning.controller;

import com.star.common.dto.PageParam;
import com.star.common.dto.PageResult;
import com.star.common.result.R;
import com.star.learning.dto.UserCourseDTO;
import com.star.learning.service.UserCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户课程控制器
 *
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/api/user-courses")
@RequiredArgsConstructor
@Tag(name = "用户课程管理", description = "用户课程查询、学习进度管理等")
public class UserCourseController {

    private final UserCourseService userCourseService;

    @GetMapping("/my")
    @Operation(summary = "获取我的课程", description = "分页获取当前用户的课程列表")
    public R<PageResult<UserCourseDTO>> getMyCourses(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        PageParam pageParam = new PageParam(current, size);
        PageResult<UserCourseDTO> result = userCourseService.getUserCourses(userId, pageParam);
        return R.ok(result);
    }

    @GetMapping("/my/{courseId}")
    @Operation(summary = "获取课程详情", description = "获取用户已购买课程的详细信息")
    public R<UserCourseDTO> getMyCourseDetail(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        UserCourseDTO course = userCourseService.getUserCourseDetail(userId, courseId);
        if (course == null) {
            return R.error("未购买该课程或课程已过期");
        }
        return R.ok(course);

    }

    @GetMapping("/check/{courseId}")
    @Operation(summary = "检查是否已购买课程", description = "检查用户是否已购买指定课程")
    public R<Boolean> checkHasCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        boolean hasCourse = userCourseService.checkUserHasCourse(userId, courseId);
        return R.ok(hasCourse);
    }

    @PutMapping("/{courseId}/progress")
    @Operation(summary = "更新课程进度", description = "更新用户课程学习进度")
    public R<Boolean> updateCourseProgress(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Parameter(description = "进度(0-100)") @RequestParam Integer progress,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        boolean result = userCourseService.updateCourseProgress(userId, courseId, progress);
        return R.ok(result, "进度更新成功");
    }

    @GetMapping("/my/count")
    @Operation(summary = "获取课程总数", description = "获取用户购买的课程总数")
    public R<Integer> getMyCourseCount(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Integer count = userCourseService.getUserCourseCount(userId);
        return R.ok(count);
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("无效的用户ID: {}", userIdHeader);
            }
        }
        
        log.warn("未能从请求中获取用户ID，使用默认值");
        return 1L;
    }
}