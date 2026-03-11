package com.star.ai.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.ai.openclaw.OpenClawSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 学习智能体工具集（6 个 @Tool 方法）
 * 同时服务于：ChatClient function calling 和 MCP Server 对外暴露给 OpenClaw
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LearningAgentTools {

    private final OpenClawSkillService skillService;
    private final ObjectMapper objectMapper;

    @Tool(description = "查询指定用户已购课程列表及章节结构，需要查课程信息时调用")
    public String courseQuery(
            @ToolParam(description = "用户名") String username,
            @ToolParam(description = "课程ID，选填，不填则查所有已购课程") @Nullable Long courseId) {
        log.info("[Tool] courseQuery: username={}, courseId={}", username, courseId);
        Object result = skillService.courseQuerySkill(username, courseId);
        String json = toJson(result);
        ToolCallRecorder.record("courseQuery", toJson(Map.of("username", str(username))), json);
        return json;
    }

    @Tool(description = "查询用户学习进度、已完成课时数、连续学习天数，需要了解学习情况时调用")
    public String learningProgressTrack(
            @ToolParam(description = "用户名") String username,
            @ToolParam(description = "课程ID，选填") @Nullable Long courseId) {
        log.info("[Tool] learningProgressTrack: username={}, courseId={}", username, courseId);
        Object result = skillService.learningProgressTrackSkill(username, courseId);
        String json = toJson(result);
        ToolCallRecorder.record("learningProgressTrack", toJson(Map.of("username", str(username))), json);
        return json;
    }

    @Tool(description = "根据课程章节和每日学习时长生成按天学习计划，用户要求制定计划时调用")
    public String learningPlanGenerate(
            @ToolParam(description = "用户名") String username,
            @ToolParam(description = "课程ID") Long courseId,
            @ToolParam(description = "每日学习小时数") Double dailyStudyHours,
            @ToolParam(description = "目标完成日期，格式 yyyy-MM-dd") String targetFinishDate) {
        log.info("[Tool] learningPlanGenerate: username={}, courseId={}, hours={}, target={}",
                username, courseId, dailyStudyHours, targetFinishDate);
        Object result = skillService.learningPlanGenerateSkill(username, courseId, dailyStudyHours, targetFinishDate);
        String json = toJson(result);
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("username", str(username));
        args.put("courseId", courseId);
        args.put("dailyStudyHours", dailyStudyHours);
        args.put("targetFinishDate", str(targetFinishDate));
        ToolCallRecorder.record("learningPlanGenerate", toJson(args), json);
        return json;
    }

    @Tool(description = "基于课程知识库回答知识点疑问，用户提问课程相关问题时调用")
    public String courseRagQa(
            @ToolParam(description = "用户的具体问题") String userQuestion,
            @ToolParam(description = "用户名，选填") @Nullable String username,
            @ToolParam(description = "课程ID，选填") @Nullable Long courseId) {
        log.info("[Tool] courseRagQa: question={}, username={}", userQuestion, username);
        Object result = skillService.courseRagQaSkill(username, courseId, userQuestion);
        String json = toJson(result);
        ToolCallRecorder.record("courseRagQa", toJson(Map.of("userQuestion", str(userQuestion))), json);
        return json;
    }

    @Tool(description = "为用户设置每日学习提醒时间")
    public String learningRemind(
            @ToolParam(description = "用户名") String username,
            @ToolParam(description = "提醒时间，如 21:00") String remindTime,
            @ToolParam(description = "关联课程ID") @Nullable Long courseId) {
        log.info("[Tool] learningRemind: username={}, time={}, courseId={}", username, remindTime, courseId);
        Object result = skillService.learningRemindSkill(username, remindTime, courseId);
        String json = toJson(result);
        ToolCallRecorder.record("learningRemind",
                toJson(Map.of("username", str(username), "remindTime", str(remindTime))), json);
        return json;
    }

    @Tool(description = "根据章节内容生成课后测验题目，用户要求做练习或测试时调用")
    public String afterClassTestGenerate(
            @ToolParam(description = "课程ID") Long courseId,
            @ToolParam(description = "章节ID") Long chapterId,
            @ToolParam(description = "题目数量，默认5") @Nullable Integer questionCount) {
        log.info("[Tool] afterClassTestGenerate: courseId={}, chapterId={}, count={}", courseId, chapterId, questionCount);
        Object result = skillService.afterClassTestGenerateSkill(courseId, chapterId, questionCount);
        String json = toJson(result);
        ToolCallRecorder.record("afterClassTestGenerate",
                toJson(Map.of("courseId", courseId, "chapterId", chapterId)), json);
        return json;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"error\":\"序列化失败: " + e.getMessage() + "\"}";
        }
    }

    private static String str(Object v) {
        return v != null ? v.toString() : "";
    }
}
