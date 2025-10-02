package com.star.ai.controller;

import com.star.ai.dto.CourseRecommendation;
import com.star.ai.dto.LearningPath;
import com.star.ai.service.CourseRecommendService;
import com.star.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 课程推荐控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
@Tag(name = "课程推荐", description = "AI智能课程推荐接口")
public class CourseRecommendController {

    private final CourseRecommendService courseRecommendService;

    /**
     * 个性化课程推荐
     */
    @GetMapping("/courses")
    @Operation(summary = "推荐课程", description = "基于用户学习历史智能推荐课程")
    public R<List<CourseRecommendation>> recommendCourses(
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "5") Integer limit,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("用户{}请求课程推荐，数量: {}", userId, limit);
        
        List<CourseRecommendation> recommendations = courseRecommendService.recommendCourses(userId, limit);
        return R.ok(recommendations, "课程推荐成功");
    }

    /**
     * 学习路径推荐
     */
    @PostMapping("/path")
    @Operation(summary = "学习路径", description = "根据学习目标生成完整学习路径")
    public R<LearningPath> recommendPath(
            @Parameter(description = "学习目标", example = "成为Java后端工程师") @RequestParam String goal,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("用户{}请求学习路径，目标: {}", userId, goal);
        
        LearningPath path = courseRecommendService.recommendLearningPath(userId, goal);
        return R.ok(path, "学习路径生成成功");
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查课程推荐服务是否正常")
    public R<String> health() {
        return R.ok("课程推荐服务运行正常");
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
