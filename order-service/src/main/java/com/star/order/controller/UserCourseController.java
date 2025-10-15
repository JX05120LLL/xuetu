package com.star.order.controller;

import com.star.common.dto.PageParam;
import com.star.common.dto.PageResult;
import com.star.common.result.R;
import com.star.order.entity.UserCourse;
import com.star.order.service.UserCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户课程管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/api/user-courses")
@RequiredArgsConstructor
@Tag(name = "用户课程管理", description = "用户购买的课程相关接口")
public class UserCourseController {

    private final UserCourseService userCourseService;

    @GetMapping("/my")
    @Operation(summary = "查询我的课程", description = "分页查询当前用户购买的课程列表")
    public R<PageResult<UserCourse>> getMyCourses(@Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
                                                  @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
                                                  HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        PageParam pageParam = new PageParam(current, size);
        PageResult<UserCourse> courses = userCourseService.getUserCourses(userId, pageParam);
        return R.ok(courses);
    }

    @GetMapping("/check/{courseId}")
    @Operation(summary = "检查是否已购买课程", description = "检查当前用户是否已购买指定课程")
    public R<Boolean> checkHasCourse(@Parameter(description = "课程ID", required = true) @PathVariable Long courseId,
                                     HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean hasCourse = userCourseService.checkUserHasCourse(userId, courseId);
        return R.ok(hasCourse);
    }

    @PutMapping("/{courseId}/progress")
    @Operation(summary = "更新学习进度", description = "更新指定课程的学习进度")
    public R<Boolean> updateProgress(@Parameter(description = "课程ID", required = true) @PathVariable Long courseId,
                                     @Parameter(description = "进度百分比(0-100)", required = true) @RequestParam Integer progress,
                                     HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean result = userCourseService.updateProgress(userId, courseId, progress);
        return R.ok(result, "学习进度更新成功");
    }

    @GetMapping("/my/count")
    @Operation(summary = "统计我的课程数量", description = "统计当前用户购买的课程总数")
    public R<Integer> countMyCourses(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Integer count = userCourseService.countUserCourses(userId);
        return R.ok(count);
    }

    @GetMapping("/course/{courseId}/users/count")
    @Operation(summary = "统计课程购买人数", description = "统计指定课程的购买用户数量")
    public R<Integer> countCourseUsers(@Parameter(description = "课程ID", required = true) @PathVariable Long courseId) {
        Integer count = userCourseService.countCourseUsers(courseId);
        return R.ok(count);
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.error("无效的用户ID格式: {}", userIdHeader, e);
                throw new RuntimeException("用户认证信息格式错误，请重新登录");
            }
        }
        
        log.error("未能从请求头中获取用户ID");
        throw new RuntimeException("用户未登录或登录已过期，请重新登录");
    }
}