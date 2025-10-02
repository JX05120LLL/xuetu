package com.star.ai.controller;

import com.star.ai.dto.LearningReport;
import com.star.ai.service.LearningAnalysisService;
import com.star.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 学习分析控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
@Tag(name = "学习分析", description = "AI智能学习分析接口")
public class LearningAnalysisController {

    private final LearningAnalysisService learningAnalysisService;

    /**
     * 生成学习分析报告
     */
    @GetMapping("/report")
    @Operation(summary = "生成学习报告", description = "AI分析用户学习数据并生成详细报告")
    public R<LearningReport> generateReport(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("用户{}请求生成学习分析报告", userId);
        
        LearningReport report = learningAnalysisService.generateLearningReport(userId);
        return R.ok(report, "学习报告生成成功");
    }

    /**
     * 获取学习建议
     */
    @GetMapping("/advice")
    @Operation(summary = "获取学习建议", description = "基于学习数据获取AI建议")
    public R<String> getLearningAdvice(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("用户{}请求学习建议", userId);
        
        String advice = learningAnalysisService.getLearningAdvice(userId);
        return R.ok(advice, "学习建议获取成功");
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查学习分析服务是否正常")
    public R<String> health() {
        return R.ok("学习分析服务运行正常");
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
