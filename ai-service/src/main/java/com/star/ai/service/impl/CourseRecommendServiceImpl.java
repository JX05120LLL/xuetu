package com.star.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.star.ai.client.QwenAIClient;
import com.star.ai.dto.*;
import com.star.ai.exception.AIServiceException;
import com.star.ai.feign.CourseServiceClient;
import com.star.ai.feign.LearningServiceClient;
import com.star.ai.service.CourseRecommendService;
import com.star.common.result.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程推荐服务实现
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseRecommendServiceImpl implements CourseRecommendService {

    private final QwenAIClient qwenAIClient;
    private final LearningServiceClient learningServiceClient;
    private final CourseServiceClient courseServiceClient;

    @Override
    public List<CourseRecommendation> recommendCourses(Long userId, Integer limit) {
        log.info("开始为用户{}推荐课程，数量: {}", userId, limit);

        try {
            // 1. 获取用户学习数据
            R<LearningStatsDTO> statsResult = learningServiceClient.getLearningStats(userId);
            LearningStatsDTO stats = statsResult.isSuccess() ? statsResult.getData() : null;

            // 2. 获取热门课程作为候选
            R<List<CourseDTO>> coursesResult = courseServiceClient.getHotCourses(20);
            if (!coursesResult.isSuccess() || coursesResult.getData() == null) {
                log.warn("获取课程列表失败");
                return new ArrayList<>();
            }

            List<CourseDTO> courses = coursesResult.getData();

            // 3. 为每门课程生成AI推荐理由
            List<CourseRecommendation> recommendations = new ArrayList<>();
            int count = 0;
            
            for (CourseDTO course : courses) {
                if (count >= limit) {
                    break;
                }

                try {
                    // 构造推荐理由Prompt
                    String reason = generateRecommendReason(course, stats);
                    
                    CourseRecommendation recommendation = CourseRecommendation.builder()
                            .courseId(course.getId())
                            .title(course.getTitle())
                            .description(course.getDescription())
                            .coverImage(course.getCoverImage())
                            .price(course.getPrice())
                            .level(course.getLevel())
                            .studentCount(course.getStudentCount())
                            .reason(reason)
                            .score(calculateScore(course, stats))
                            .suitable(isSuitable(course, stats))
                            .build();

                    recommendations.add(recommendation);
                    count++;
                    
                } catch (Exception e) {
                    log.warn("生成课程{}推荐理由失败: {}", course.getId(), e.getMessage());
                }
            }

            log.info("课程推荐生成成功，共推荐{}门课程", recommendations.size());
            return recommendations;

        } catch (Exception e) {
            log.error("课程推荐失败", e);
            throw new AIServiceException("课程推荐失败: " + e.getMessage());
        }
    }

    @Override
    public LearningPath recommendLearningPath(Long userId, String goal) {
        log.info("为用户{}生成学习路径，目标: {}", userId, goal);

        try {
            // 1. 获取用户学习数据
            R<LearningStatsDTO> statsResult = learningServiceClient.getLearningStats(userId);
            LearningStatsDTO stats = statsResult.isSuccess() ? statsResult.getData() : null;

            // 2. 构造学习路径Prompt
            String prompt = buildLearningPathPrompt(goal, stats);

            // 3. 调用AI生成学习路径
            String aiResponse = qwenAIClient.chat(prompt, null);

            // 4. 解析AI响应，构建学习路径
            LearningPath path = parseLearningPath(goal, aiResponse);

            log.info("学习路径生成成功: userId={}, goal={}", userId, goal);
            return path;

        } catch (Exception e) {
            log.error("生成学习路径失败", e);
            throw new AIServiceException("生成学习路径失败: " + e.getMessage());
        }
    }

    /**
     * 生成推荐理由
     */
    private String generateRecommendReason(CourseDTO course, LearningStatsDTO stats) {
        String prompt;
        
        if (stats != null && stats.getTotalLearningTime() > 0) {
            prompt = String.format(
                "用户已学习%d小时，完成%d个课时。" +
                "课程《%s》是%s难度。" +
                "请用一句话（30字内）说明为什么推荐这门课程给该用户。",
                stats.getTotalLearningTime() / 60,
                stats.getCompletedLessons(),
                course.getTitle(),
                getLevelName(course.getLevel())
            );
        } else {
            prompt = String.format(
                "课程《%s》是%s难度，有%d人学习。" +
                "请用一句话（30字内）说明为什么推荐这门课程给新用户。",
                course.getTitle(),
                getLevelName(course.getLevel()),
                course.getStudentCount()
            );
        }

        try {
            return qwenAIClient.chat(prompt, null);
        } catch (Exception e) {
            // 如果AI调用失败，返回默认推荐理由
            return String.format("热门%s课程，%d人已学习，适合提升技能",
                    getLevelName(course.getLevel()), course.getStudentCount());
        }
    }

    /**
     * 计算推荐分数
     */
    private Integer calculateScore(CourseDTO course, LearningStatsDTO stats) {
        int score = 60; // 基础分

        // 根据学习人数加分
        if (course.getStudentCount() > 1000) {
            score += 20;
        } else if (course.getStudentCount() > 500) {
            score += 10;
        }

        // 根据难度和用户水平匹配度加分
        if (stats != null) {
            int userLevel = getUserLevel(stats);
            if (course.getLevel().equals(userLevel)) {
                score += 20; // 难度匹配
            }
        }

        return Math.min(score, 100);
    }

    /**
     * 判断是否适合
     */
    private Boolean isSuitable(CourseDTO course, LearningStatsDTO stats) {
        if (stats == null || stats.getTotalLearningTime() == 0) {
            return course.getLevel() == 0; // 新手适合初级
        }

        int userLevel = getUserLevel(stats);
        return Math.abs(course.getLevel() - userLevel) <= 1;
    }

    /**
     * 构建学习路径Prompt
     */
    private String buildLearningPathPrompt(String goal, LearningStatsDTO stats) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("你是专业的学习规划师，请为用户制定详细的学习路径。\n\n");
        prompt.append("学习目标: ").append(goal).append("\n");
        
        if (stats != null && stats.getTotalLearningTime() > 0) {
            prompt.append(String.format("用户基础: 已学习%d小时，完成%d个课时\n", 
                    stats.getTotalLearningTime() / 60, stats.getCompletedLessons()));
        } else {
            prompt.append("用户基础: 零基础\n");
        }
        
        prompt.append("\n请按以下格式输出学习路径：\n\n");
        prompt.append("【预计时间】\n");
        prompt.append("完成天数: X天\n");
        prompt.append("学习时长: X小时\n\n");
        prompt.append("【第一阶段: 阶段名称】\n");
        prompt.append("阶段描述: (描述)\n");
        prompt.append("推荐课程: 课程1, 课程2, 课程3\n");
        prompt.append("预计时长: X小时\n");
        prompt.append("学习要点: 要点1; 要点2; 要点3\n\n");
        prompt.append("【第二阶段: 阶段名称】\n");
        prompt.append("...(同上格式)\n\n");
        prompt.append("【第三阶段: 阶段名称】\n");
        prompt.append("...(同上格式)\n\n");
        prompt.append("要求: 至少3个阶段，循序渐进，具体可操作");
        
        return prompt.toString();
    }

    /**
     * 解析学习路径
     */
    private LearningPath parseLearningPath(String goal, String aiResponse) {
        LearningPath path = new LearningPath();
        path.setGoal(goal);
        path.setPathDescription(aiResponse);

        try {
            // 提取预计时间
            String timeSection = extractSection(aiResponse, "【预计时间】", "【第一阶段");
            if (timeSection != null) {
                path.setEstimatedDays(extractNumber(timeSection, "完成天数"));
                path.setEstimatedHours(extractNumber(timeSection, "学习时长"));
            }

            // 提取各个阶段
            List<LearningPath.LearningStage> stages = new ArrayList<>();
            int stageNum = 1;
            while (true) {
                String stageMarker = "【第" + getChineseNumber(stageNum) + "阶段";
                String nextStageMarker = "【第" + getChineseNumber(stageNum + 1) + "阶段";
                
                String stageContent = extractSection(aiResponse, stageMarker, nextStageMarker);
                if (stageContent == null) {
                    break;
                }

                LearningPath.LearningStage stage = parseStage(stageContent, stageMarker);
                if (stage != null) {
                    stages.add(stage);
                }
                stageNum++;
            }

            path.setStages(stages);

        } catch (Exception e) {
            log.warn("解析学习路径失败，使用默认值", e);
            path.setEstimatedDays(90);
            path.setEstimatedHours(200);
            path.setStages(new ArrayList<>());
        }

        return path;
    }

    /**
     * 解析阶段信息
     */
    private LearningPath.LearningStage parseStage(String content, String stageMarker) {
        try {
            String stageName = extractAfterMarker(stageMarker, ":");
            String description = extractValue(content, "阶段描述");
            String coursesStr = extractValue(content, "推荐课程");
            Integer hours = extractNumber(content, "预计时长");
            String keyPointsStr = extractValue(content, "学习要点");

            List<String> courses = coursesStr != null ? 
                    Arrays.asList(coursesStr.split("[,，]")) : new ArrayList<>();
            List<String> keyPoints = keyPointsStr != null ? 
                    Arrays.asList(keyPointsStr.split("[;；]")) : new ArrayList<>();

            return LearningPath.LearningStage.builder()
                    .stageName(stageName)
                    .description(description)
                    .courses(courses)
                    .hours(hours)
                    .keyPoints(keyPoints)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    // 辅助方法
    private String getLevelName(Integer level) {
        switch (level) {
            case 0: return "初级";
            case 1: return "中级";
            case 2: return "高级";
            default: return "未知";
        }
    }

    private int getUserLevel(LearningStatsDTO stats) {
        int hours = stats.getTotalLearningTime() / 60;
        if (hours < 20) return 0; // 初级
        if (hours < 100) return 1; // 中级
        return 2; // 高级
    }

    private String extractSection(String text, String start, String end) {
        int startIdx = text.indexOf(start);
        if (startIdx == -1) return null;
        
        int endIdx = end != null ? text.indexOf(end, startIdx + start.length()) : text.length();
        if (endIdx == -1) endIdx = text.length();
        
        return text.substring(startIdx + start.length(), endIdx).trim();
    }

    private String extractValue(String text, String key) {
        String pattern = key + "[:：]\\s*";
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.contains(key)) {
                return line.replaceFirst(pattern, "").trim();
            }
        }
        return null;
    }

    private Integer extractNumber(String text, String key) {
        String value = extractValue(text, key);
        if (value == null) return 0;
        
        try {
            return Integer.parseInt(value.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private String extractAfterMarker(String text, String marker) {
        int idx = text.indexOf(marker);
        return idx != -1 ? text.substring(idx + marker.length()).trim() : "";
    }

    private String getChineseNumber(int num) {
        String[] numbers = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        return num < numbers.length ? numbers[num] : String.valueOf(num);
    }
}
