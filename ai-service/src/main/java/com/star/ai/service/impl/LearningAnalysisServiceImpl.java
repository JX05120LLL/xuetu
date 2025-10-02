package com.star.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.star.ai.client.QwenAIClient;
import com.star.ai.dto.LearningReport;
import com.star.ai.dto.LearningStatsDTO;
import com.star.ai.dto.UserCourseProgressDTO;
import com.star.ai.exception.AIServiceException;
import com.star.ai.feign.LearningServiceClient;
import com.star.ai.service.LearningAnalysisService;
import com.star.common.result.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学习分析服务实现
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LearningAnalysisServiceImpl implements LearningAnalysisService {

    private final QwenAIClient qwenAIClient;
    private final LearningServiceClient learningServiceClient;

    @Override
    public LearningReport generateLearningReport(Long userId) {
        log.info("开始生成用户{}的学习分析报告", userId);

        try {
            // 1. 获取用户学习数据
            R<LearningStatsDTO> statsResult = learningServiceClient.getLearningStats(userId);
            if (!statsResult.isSuccess() || statsResult.getData() == null) {
                log.warn("获取用户学习统计失败: {}", statsResult.getMessage());
                return buildDefaultReport(userId);
            }

            LearningStatsDTO stats = statsResult.getData();

            // 2. 获取课程进度（暂不实现，learning-service还没有这个接口）
            List<UserCourseProgressDTO> courseProgress = new ArrayList<>();
            // TODO: 等learning-service添加课程进度列表接口后再启用
            /*
            try {
                R<List<UserCourseProgressDTO>> progressResult = learningServiceClient.getUserCourseProgress(userId);
                if (progressResult.isSuccess() && progressResult.getData() != null) {
                    courseProgress = progressResult.getData();
                }
            } catch (Exception e) {
                log.warn("获取课程进度失败，跳过: {}", e.getMessage());
            }
            */

            // 3. 构造分析Prompt
            String prompt = buildAnalysisPrompt(stats, courseProgress);

            // 4. 调用AI分析
            String aiAnalysis = qwenAIClient.chat(prompt, null);

            // 5. 解析AI回答，构建报告
            LearningReport report = parseAIAnalysis(userId, stats, aiAnalysis);

            log.info("学习分析报告生成成功: userId={}", userId);
            return report;

        } catch (Exception e) {
            log.error("生成学习分析报告失败", e);
            throw new AIServiceException("生成学习报告失败: " + e.getMessage());
        }
    }

    @Override
    public String getLearningAdvice(Long userId) {
        log.info("获取用户{}的学习建议", userId);

        try {
            R<LearningStatsDTO> statsResult = learningServiceClient.getLearningStats(userId);
            if (!statsResult.isSuccess() || statsResult.getData() == null) {
                return "暂时无法获取学习数据，请稍后再试";
            }

            LearningStatsDTO stats = statsResult.getData();

            String prompt = String.format(
                "作为学习顾问，请根据以下数据给出3-5条具体的学习建议：\n" +
                "- 总学习时长: %d小时\n" +
                "- 完成课时: %d个\n" +
                "- 连续学习: %d天\n" +
                "- 本周学习: %d小时\n\n" +
                "要求：建议要具体、可操作，每条控制在30字以内",
                stats.getTotalLearningTime() / 60,
                stats.getCompletedLessons(),
                stats.getContinuousDays(),
                stats.getWeekLearningTime() / 60
            );

            return qwenAIClient.chat(prompt, null);

        } catch (Exception e) {
            log.error("获取学习建议失败", e);
            return "获取学习建议失败，请稍后重试";
        }
    }

    /**
     * 构建分析Prompt
     */
    private String buildAnalysisPrompt(LearningStatsDTO stats, List<UserCourseProgressDTO> courseProgress) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("你是学途教育平台的AI学习顾问，请分析以下学习数据并生成专业的学习报告：\n\n");
        
        // 学习概况
        prompt.append("【学习概况】\n");
        prompt.append(String.format("- 总学习时长: %d小时\n", stats.getTotalLearningTime() / 60));
        prompt.append(String.format("- 完成课时数: %d个\n", stats.getCompletedLessons()));
        prompt.append(String.format("- 连续学习天数: %d天\n", stats.getContinuousDays()));
        prompt.append(String.format("- 本周学习时长: %d小时\n", stats.getWeekLearningTime() / 60));
        prompt.append(String.format("- 正在学习课程: %d门\n\n", stats.getLearningCourses()));
        
        // 课程进度（如果有）
        if (!courseProgress.isEmpty()) {
            prompt.append("【课程学习进度】\n");
            for (UserCourseProgressDTO progress : courseProgress) {
                prompt.append(String.format("- %s: %d%%\n", progress.getCourseName(), progress.getProgress()));
            }
            prompt.append("\n");
        }
        
        // 要求AI给出的分析内容
        prompt.append("请按以下格式输出分析报告：\n\n");
        prompt.append("【学习状态评价】\n");
        prompt.append("(给出2-3句总体评价)\n\n");
        prompt.append("【优势总结】\n");
        prompt.append("(列出1-2条优势)\n\n");
        prompt.append("【改进建议】\n");
        prompt.append("1. (具体建议1)\n");
        prompt.append("2. (具体建议2)\n");
        prompt.append("3. (具体建议3)\n\n");
        prompt.append("【下周学习计划】\n");
        prompt.append("(给出具体的学习计划安排)\n\n");
        prompt.append("要求：语言友好、鼓励性强、建议具体可操作");
        
        return prompt.toString();
    }

    /**
     * 解析AI分析结果
     */
    private LearningReport parseAIAnalysis(Long userId, LearningStatsDTO stats, String aiAnalysis) {
        LearningReport report = new LearningReport();
        report.setUserId(userId);
        report.setLearningTime(stats.getTotalLearningTime() / 60);
        report.setCompletedCourses(stats.getLearningCourses());
        report.setContinuousDays(stats.getContinuousDays());
        report.setAiAnalysis(aiAnalysis);

        // 简单解析AI回答（按格式提取）
        try {
            // 提取学习状态评价
            String evaluation = extractSection(aiAnalysis, "【学习状态评价】", "【优势总结】");
            report.setEvaluation(evaluation != null ? evaluation.trim() : "学习状态良好");

            // 提取优势总结
            String strengths = extractSection(aiAnalysis, "【优势总结】", "【改进建议】");
            report.setStrengths(strengths != null ? strengths.trim() : "保持学习热情");

            // 提取改进建议
            String advicesText = extractSection(aiAnalysis, "【改进建议】", "【下周学习计划】");
            List<String> advices = parseAdvices(advicesText);
            report.setAdvices(advices);

            // 提取下周计划
            String nextPlan = extractSection(aiAnalysis, "【下周学习计划】", null);
            report.setNextWeekPlan(nextPlan != null ? nextPlan.trim() : "继续保持学习节奏");

        } catch (Exception e) {
            log.warn("解析AI分析结果失败，使用默认值", e);
        }

        return report;
    }

    /**
     * 提取章节内容
     */
    private String extractSection(String text, String startMarker, String endMarker) {
        int startIndex = text.indexOf(startMarker);
        if (startIndex == -1) {
            return null;
        }

        startIndex += startMarker.length();

        int endIndex = endMarker != null ? text.indexOf(endMarker, startIndex) : text.length();
        if (endIndex == -1) {
            endIndex = text.length();
        }

        return text.substring(startIndex, endIndex).trim();
    }

    /**
     * 解析建议列表
     */
    private List<String> parseAdvices(String advicesText) {
        if (StrUtil.isBlank(advicesText)) {
            return Arrays.asList("继续保持学习热情", "制定合理的学习计划", "定期复习巩固知识");
        }

        return Arrays.stream(advicesText.split("\n"))
                .map(String::trim)
                .filter(line -> line.matches("^\\d+\\..*"))
                .map(line -> line.replaceFirst("^\\d+\\.\\s*", ""))
                .collect(Collectors.toList());
    }

    /**
     * 构建默认报告（当数据获取失败时）
     */
    private LearningReport buildDefaultReport(Long userId) {
        return LearningReport.builder()
                .userId(userId)
                .learningTime(0)
                .completedCourses(0)
                .continuousDays(0)
                .evaluation("暂时无法获取学习数据")
                .strengths("继续努力学习")
                .advices(Arrays.asList("开始学习之旅", "选择感兴趣的课程", "制定学习计划"))
                .nextWeekPlan("选择课程开始学习")
                .aiAnalysis("暂时无法生成分析报告")
                .build();
    }
}
