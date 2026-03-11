package com.star.ai.openclaw;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.ai.openclaw.dto.SkillRequestExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OpenClaw 技能 HTTP 入口
 * 设计文档：功能一 6 个学习智能体 Skill + 功能二 4 个运维 Skill，供 OpenClaw 网关按 invokeUrl 调用。
 */
@Slf4j
@RestController
@RequestMapping("/openclaw/skill")
@RequiredArgsConstructor
@Tag(name = "OpenClaw 技能", description = "自主学习智能体与智能运维 Agent 技能入口")
public class OpenClawSkillController {

    private final OpenClawSkillService skillService;
    private final ObjectMapper objectMapper;

    /**
     * 兼容 JSON 与 form-urlencoded：从请求体或请求参数构建 Map，供各 skill 统一取参
     */
    private Map<String, Object> bodyMap(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            try {
                String body = request.getReader().lines().collect(Collectors.joining("\n"));
                if (body == null || body.isBlank()) return new HashMap<>();
                return objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.warn("解析 JSON body 失败", e);
                return new HashMap<>();
            }
        }
        Map<String, Object> map = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0) map.put(k, v[0]);
        });
        return map;
    }

    private static String str(Map<String, Object> body, String key) {
        Object v = body.get(key);
        return v != null ? v.toString().trim() : null;
    }

    /** 支持多 key 别名，表单可能用 user_name / username 等 */
    private static String str(Map<String, Object> body, String... keys) {
        for (String key : keys) {
            String s = str(body, key);
            if (s != null && !s.isEmpty()) return s;
        }
        return null;
    }

    private static Long longVal(Map<String, Object> body, String key) {
        Object v = body.get(key);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(v.toString()); } catch (NumberFormatException e) { return null; }
    }

    private static Double doubleVal(Map<String, Object> body, String key) {
        Object v = body.get(key);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (NumberFormatException e) { return null; }
    }

    private static Integer intVal(Map<String, Object> body, String key) {
        Object v = body.get(key);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(v.toString()); } catch (NumberFormatException e) { return null; }
    }

    @PostMapping(value = "/course_query_skill", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @Operation(summary = "课程查询技能", description = "根据用户名查询已购课程列表、课程详情、章节结构",
            requestBody = @RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded", schema = @Schema(implementation = SkillRequestExamples.CourseQuery.class))))
    public Object courseQuerySkill(HttpServletRequest request) {
        Map<String, Object> body = bodyMap(request);
        String username = str(body, "username", "user_name");
        Long courseId = longVal(body, "courseId");
        log.info("[OpenClaw] skill=course_query_skill, username={}, courseId={}, paramKeys={}", username, courseId, body.keySet());
        if (username == null || username.isBlank()) {
            return Map.of("error", "请提供参数 username（在请求参数或 JSON 中填写 username，例如 testuser1）");
        }
        return skillService.courseQuerySkill(username, courseId);
    }

    @PostMapping(value = "/learning_progress_track_skill", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @Operation(summary = "学习进度跟踪技能", description = "查询章节学习进度、已完成课时、测验成绩等",
            requestBody = @RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded", schema = @Schema(implementation = SkillRequestExamples.LearningProgressTrack.class))))
    public Object learningProgressTrackSkill(HttpServletRequest request) {
        Map<String, Object> body = bodyMap(request);
        String username = str(body, "username", "user_name");
        Long courseId = longVal(body, "courseId");
        log.info("[OpenClaw] skill=learning_progress_track_skill, username={}, courseId={}, paramKeys={}", username, courseId, body.keySet());
        if (username == null || username.isBlank()) {
            return Map.of("error", "请提供参数 username");
        }
        return skillService.learningProgressTrackSkill(username, courseId);
    }

    @PostMapping(value = "/learning_plan_generate_skill", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @Operation(summary = "学习计划生成技能", description = "根据课程、每日时长、目标日期生成每日学习计划",
            requestBody = @RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded", schema = @Schema(implementation = SkillRequestExamples.LearningPlanGenerate.class))))
    public Object learningPlanGenerateSkill(HttpServletRequest request) {
        Map<String, Object> body = bodyMap(request);
        String username = str(body, "username", "user_name");
        Long courseId = longVal(body, "courseId");
        Double dailyStudyHours = doubleVal(body, "daily_study_hours");
        String targetFinishDate = str(body, "target_finish_date");
        log.info("[OpenClaw] skill=learning_plan_generate_skill, username={}, courseId={}, paramKeys={}", username, courseId, body.keySet());
        if (username == null || username.isBlank()) {
            return Map.of("error", "请提供参数 username");
        }
        return skillService.learningPlanGenerateSkill(username, courseId, dailyStudyHours, targetFinishDate);
    }

    @PostMapping(value = "/course_rag_qa_skill", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @Operation(summary = "课程知识库问答技能", description = "基于 RAG 解答课程知识点疑问",
            requestBody = @RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded", schema = @Schema(implementation = SkillRequestExamples.CourseRagQa.class))))
    public Object courseRagQaSkill(HttpServletRequest request) {
        Map<String, Object> body = bodyMap(request);
        String username = str(body, "username", "user_name");
        Long courseId = longVal(body, "courseId");
        String userQuestion = str(body, "user_question", "userQuestion");
        log.info("[OpenClaw] skill=course_rag_qa_skill, username={}, userQuestion={}, paramKeys={}", username, userQuestion != null ? "(present)" : null, body.keySet());
        return skillService.courseRagQaSkill(username, courseId, userQuestion);
    }

    @PostMapping(value = "/learning_remind_skill", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @Operation(summary = "学习提醒技能", description = "设置每日学习定时提醒",
            requestBody = @RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded", schema = @Schema(implementation = SkillRequestExamples.LearningRemind.class))))
    public Object learningRemindSkill(HttpServletRequest request) {
        Map<String, Object> body = bodyMap(request);
        String username = str(body, "username", "user_name");
        String remindTime = str(body, "remind_time", "remindTime");
        Long courseId = longVal(body, "course_id");
        log.info("[OpenClaw] skill=learning_remind_skill, username={}, remindTime={}, paramKeys={}", username, remindTime, body.keySet());
        if (username == null || username.isBlank()) {
            return Map.of("error", "请提供参数 username");
        }
        return skillService.learningRemindSkill(username, remindTime, courseId);
    }

    @PostMapping(value = "/after_class_test_generate_skill", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @Operation(summary = "课后测验生成技能", description = "基于章节内容生成选择题、简答题与解析",
            requestBody = @RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded", schema = @Schema(implementation = SkillRequestExamples.AfterClassTestGenerate.class))))
    public Object afterClassTestGenerateSkill(HttpServletRequest request) {
        Map<String, Object> body = bodyMap(request);
        Long courseId = longVal(body, "course_id");
        Long chapterId = longVal(body, "chapter_id");
        Integer questionCount = intVal(body, "question_count");
        log.info("[OpenClaw] skill=after_class_test_generate_skill, courseId={}, chapterId={}, paramKeys={}", courseId, chapterId, body.keySet());
        return skillService.afterClassTestGenerateSkill(courseId, chapterId, questionCount);
    }

    @PostMapping(value = "/service_health_check_skill", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @Operation(summary = "微服务健康检查技能", description = "查询 Nacos 注册的服务实例健康状态",
            requestBody = @RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded", schema = @Schema(implementation = SkillRequestExamples.ServiceHealthCheck.class))))
    public Object serviceHealthCheckSkill(HttpServletRequest request) {
        Map<String, Object> body = bodyMap(request);
        String serviceName = str(body, "service_name", "serviceName");
        log.info("[OpenClaw] skill=service_health_check_skill, serviceName={}, paramKeys={}", serviceName, body.keySet());
        return skillService.serviceHealthCheckSkill(serviceName);
    }

    @PostMapping(value = "/gateway_interface_monitor_skill", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @Operation(summary = "网关接口监控技能", description = "查询接口 QPS、响应时间、错误率等",
            requestBody = @RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded", schema = @Schema(implementation = SkillRequestExamples.GatewayInterfaceMonitor.class))))
    public Object gatewayInterfaceMonitorSkill(HttpServletRequest request) {
        Map<String, Object> body = bodyMap(request);
        String timeRange = str(body, "time_range", "timeRange");
        String serviceName = str(body, "service_name", "serviceName");
        log.info("[OpenClaw] skill=gateway_interface_monitor_skill, paramKeys={}", body.keySet());
        return skillService.gatewayInterfaceMonitorSkill(timeRange, serviceName);
    }

    @PostMapping(value = "/middleware_status_check_skill", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @Operation(summary = "中间件状态排查技能", description = "查询 Redis、RocketMQ 运行状态",
            requestBody = @RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded", schema = @Schema(implementation = SkillRequestExamples.MiddlewareStatusCheck.class))))
    public Object middlewareStatusCheckSkill(HttpServletRequest request) {
        Map<String, Object> body = bodyMap(request);
        String middlewareType = str(body, "middleware_type", "middlewareType");
        log.info("[OpenClaw] skill=middleware_status_check_skill, middlewareType={}, paramKeys={}", middlewareType, body.keySet());
        return skillService.middlewareStatusCheckSkill(middlewareType);
    }

    @PostMapping(value = "/exception_log_query_skill", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @Operation(summary = "异常日志查询技能", description = "检索指定时间范围内的异常日志",
            requestBody = @RequestBody(content = @Content(mediaType = "application/x-www-form-urlencoded", schema = @Schema(implementation = SkillRequestExamples.ExceptionLogQuery.class))))
    public Object exceptionLogQuerySkill(HttpServletRequest request) {
        Map<String, Object> body = bodyMap(request);
        String timeRange = str(body, "time_range", "timeRange");
        String serviceName = str(body, "service_name", "serviceName");
        String keyword = str(body, "keyword");
        log.info("[OpenClaw] skill=exception_log_query_skill, paramKeys={}", body.keySet());
        return skillService.exceptionLogQuerySkill(timeRange, serviceName, keyword);
    }
}
