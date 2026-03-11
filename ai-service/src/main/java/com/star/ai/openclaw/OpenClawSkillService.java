package com.star.ai.openclaw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.ai.dto.*;
import com.star.ai.feign.*;
import com.star.common.result.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OpenClaw 技能实现服务
 * 功能一：6 个学习智能体 Skill；功能二：4 个运维 Skill
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenClawSkillService {

    private final UserServiceClient userServiceClient;
    private final CourseServiceClient courseServiceClient;
    private final ChapterClient chapterClient;
    private final OrderUserCourseClient orderUserCourseClient;
    private final LearningServiceClient learningServiceClient;
    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;

    @Value("${spring.cloud.nacos.discovery.server-addr:localhost:8848}")
    private String nacosServerAddr;

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    private final RestTemplate restTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    // ---------- 功能一：学习智能体 Skill ----------

    /**
     * course_query_skill：根据用户名查询已购课程列表、课程详情、章节结构
     */
    public Object courseQuerySkill(String username, Long courseId) {
        final Long effectiveCourseId = (courseId != null && courseId <= 0) ? null : courseId; // LLM 未传时可能为 0，视为不过滤
        Long userId = resolveUserId(username);
        if (userId == null) return Map.of("error", "用户不存在: " + username);

        try {
            R<com.star.common.dto.PageResult<UserCourseSummaryDTO>> myRes = orderUserCourseClient.getMyCourses(userId, 1, 100);
            if (myRes == null || myRes.getData() == null || myRes.getData().getRecords() == null) {
                return Map.of("message", "暂无已购课程", "courses", List.of());
            }
            List<UserCourseSummaryDTO> list = myRes.getData().getRecords();
            if (list.isEmpty()) return Map.of("message", "暂无已购课程", "courses", List.of());

            List<Map<String, Object>> courses = new ArrayList<>();
            for (UserCourseSummaryDTO uc : list) {
                if (effectiveCourseId != null && !uc.getCourseId().equals(effectiveCourseId)) continue;
                R<CourseDTO> detailRes = courseServiceClient.getCourseDetail(uc.getCourseId());
                R<List<ChapterDTO>> chapterRes = chapterClient.getChaptersByCourseId(uc.getCourseId());
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("courseId", uc.getCourseId());
                item.put("purchaseTime", uc.getPurchaseTime());
                item.put("expireTime", uc.getExpireTime());
                item.put("progress", uc.getProgress());
                if (detailRes != null && detailRes.getData() != null) {
                    item.put("courseName", detailRes.getData().getTitle());
                }
                if (chapterRes != null && chapterRes.getData() != null) {
                    item.put("chapters", chapterRes.getData().stream()
                            .map(c -> Map.of("id", c.getId(), "title", c.getTitle(), "sortOrder", c.getSortOrder() != null ? c.getSortOrder() : 0))
                            .collect(Collectors.toList()));
                }
                courses.add(item);
            }
            return Map.of("username", username, "courses", courses);
        } catch (Exception e) {
            log.error("[course_query_skill] error", e);
            return Map.of("error", "查询失败: " + e.getMessage());
        }
    }

    /**
     * learning_progress_track_skill：学习进度、已完成课时、测验成绩等
     */
    public Object learningProgressTrackSkill(String username, Long courseId) {
        final Long effectiveCourseId = (courseId != null && courseId <= 0) ? null : courseId;
        Long userId = resolveUserId(username);
        if (userId == null) return Map.of("error", "用户不存在: " + username);

        try {
            R<LearningStatsDTO> statsRes = learningServiceClient.getLearningStats(userId);
            // 从 order-service 获取课程进度列表（order 的 user_course 表存储 progress 字段）
            R<com.star.common.dto.PageResult<UserCourseSummaryDTO>> orderRes =
                    orderUserCourseClient.getMyCourses(userId, 1, 100);
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("username", username);
            if (statsRes != null && statsRes.getData() != null) {
                LearningStatsDTO s = statsRes.getData();
                out.put("totalLearningTime", s.getTotalLearningTime());
                out.put("completedLessons", s.getCompletedLessons());
                out.put("continuousDays", s.getContinuousDays());
                out.put("averageProgress", s.getAverageProgress());
            }
            if (orderRes != null && orderRes.getData() != null && orderRes.getData().getRecords() != null) {
                List<UserCourseSummaryDTO> plist = orderRes.getData().getRecords();
                if (effectiveCourseId != null) plist = plist.stream().filter(p -> effectiveCourseId.equals(p.getCourseId())).collect(Collectors.toList());
                out.put("courseProgressList", plist.stream()
                        .map(p -> Map.of("courseId", p.getCourseId(), "progress", p.getProgress() != null ? p.getProgress() : 0))
                        .collect(Collectors.toList()));
            }
            if (effectiveCourseId != null) {
                R<Double> cp = learningServiceClient.getCourseProgress(userId, effectiveCourseId);
                if (cp != null && cp.getData() != null) out.put("courseProgressPercent", cp.getData());
            }
            return out;
        } catch (Exception e) {
            log.error("[learning_progress_track_skill] error", e);
            return Map.of("error", "查询失败: " + e.getMessage());
        }
    }

    /**
     * learning_plan_generate_skill：根据课程、每日时长、目标日期生成每日学习计划
     */
    public Object learningPlanGenerateSkill(String username, Long courseId, Double dailyStudyHours, String targetFinishDate) {
        Long userId = resolveUserId(username);
        if (userId == null) return Map.of("error", "用户不存在: " + username);
        if (courseId == null || courseId <= 0) return Map.of("error", "courseId 必填");
        if (dailyStudyHours == null || dailyStudyHours <= 0) return Map.of("error", "daily_study_hours 必填且大于0");
        if (targetFinishDate == null || targetFinishDate.isBlank()) return Map.of("error", "target_finish_date 必填");

        try {
            R<CourseDTO> courseRes = courseServiceClient.getCourseDetail(courseId);
            R<List<ChapterDTO>> chapterRes = chapterClient.getChaptersByCourseId(courseId);
            if (courseRes == null || courseRes.getData() == null) return Map.of("error", "课程不存在");
            List<ChapterDTO> chapters = chapterRes != null && chapterRes.getData() != null ? chapterRes.getData() : List.of();
            LocalDate target = LocalDate.parse(targetFinishDate.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate start = LocalDate.now();
            if (!start.isBefore(target) && !start.equals(target)) {
                return Map.of("error", "目标完成日期必须晚于今天");
            }
            int totalMinutes = chapters.stream()
                    .mapToInt(c -> c.getLessons() != null ? c.getLessons().stream().mapToInt(l -> l.getDuration() != null ? l.getDuration() : 0).sum() : 0)
                    .sum();
            int dailyMinutes = (int) (dailyStudyHours * 60);
            int days = Math.max(1, (int) Math.ceil((double) totalMinutes / dailyMinutes));
            List<Map<String, Object>> plan = new ArrayList<>();
            int remaining = totalMinutes;
            int dayIndex = 0;
            for (ChapterDTO ch : chapters) {
                int chapterMinutes = ch.getLessons() != null ? ch.getLessons().stream().mapToInt(l -> l.getDuration() != null ? l.getDuration() : 0).sum() : 0;
                if (chapterMinutes <= 0) continue;
                LocalDate d = start.plusDays(dayIndex);
                if (d.isAfter(target)) break;
                int assign = Math.min(dailyMinutes, Math.min(chapterMinutes, remaining));
                plan.add(Map.of(
                        "date", d.toString(),
                        "chapterId", ch.getId(),
                        "chapterTitle", ch.getTitle() != null ? ch.getTitle() : "",
                        "studyMinutes", assign,
                        "goal", "完成《" + (ch.getTitle() != null ? ch.getTitle() : "") + "》约 " + assign + " 分钟"
                ));
                remaining -= assign;
                if (assign >= chapterMinutes) dayIndex++;
                if (remaining <= 0) break;
            }
            return Map.of(
                    "username", username,
                    "courseId", courseId,
                    "courseName", courseRes.getData().getTitle(),
                    "dailyStudyHours", dailyStudyHours,
                    "targetFinishDate", targetFinishDate,
                    "totalMinutes", totalMinutes,
                    "planDays", days,
                    "dailyPlan", plan
            );
        } catch (Exception e) {
            log.error("[learning_plan_generate_skill] error", e);
            return Map.of("error", "生成失败: " + e.getMessage());
        }
    }

    /**
     * course_rag_qa_skill：课程知识库问答
     */
    public Object courseRagQaSkill(String username, Long courseId, String userQuestion) {
        if (userQuestion == null || userQuestion.isBlank()) return Map.of("error", "user_question 必填");

        try {
            List<Document> docs = vectorStore.similaritySearch(
                    SearchRequest.builder().query(userQuestion).topK(5).build());
            if (docs.isEmpty()) {
                return Map.of("hit", false, "answer", "知识库中暂无相关内容，建议结合课程教材或联系老师。", "sources", List.of());
            }
            List<Map<String, Object>> sources = docs.stream()
                    .map(d -> Map.<String, Object>of("text", d.getText(), "metadata", d.getMetadata()))
                    .collect(Collectors.toList());
            String answer = docs.stream().map(Document::getText).collect(Collectors.joining("\n\n"));
            return Map.of("hit", true, "answer", answer, "sources", sources);
        } catch (Exception e) {
            log.error("[course_rag_qa_skill] error", e);
            return Map.of("error", "检索失败: " + e.getMessage());
        }
    }

    /**
     * learning_remind_skill：学习提醒设置（占位实现，可对接短信/邮件或 OpenClaw 定时任务）
     */
    public Object learningRemindSkill(String username, String remindTime, Long courseId) {
        Long userId = resolveUserId(username);
        if (userId == null) return Map.of("error", "用户不存在: " + username);
        return Map.of(
                "message", "提醒已设置（当前为占位实现，可对接短信/邮件或 OpenClaw 定时任务）",
                "username", username,
                "remindTime", remindTime,
                "courseId", courseId,
                "nextRemindTime", remindTime
        );
    }

    /**
     * after_class_test_generate_skill：课后测验生成（基于 RAG 检索章节相关内容生成题目）
     */
    public Object afterClassTestGenerateSkill(Long courseId, Long chapterId, Integer questionCount) {
        if (courseId == null || courseId <= 0 || chapterId == null || chapterId <= 0) return Map.of("error", "course_id 与 chapter_id 必填");
        int topK = questionCount != null && questionCount > 0 ? Math.min(questionCount, 10) : 5;
        try {
            String query = "章节知识点 重点 考点";
            List<Document> docs = vectorStore.similaritySearch(
                    SearchRequest.builder().query(query).topK(topK).build());
            List<Map<String, Object>> questions = new ArrayList<>();
            for (int i = 0; i < Math.min(docs.size(), topK); i++) {
                Document d = docs.get(i);
                questions.add(Map.of(
                        "type", "简答题",
                        "content", "请根据所学内容简述：" + (d.getText().length() > 80 ? d.getText().substring(0, 80) + "..." : d.getText()),
                        "answer", "参考答案见课程内容",
                        "knowledgePoint", d.getMetadata() != null ? d.getMetadata().getOrDefault("source", "") : ""
                ));
            }
            if (questions.isEmpty()) questions.add(Map.of("type", "简答题", "content", "请简述本章核心要点。", "answer", "请结合章节内容作答。", "knowledgePoint", "本章内容"));
            return Map.of("courseId", courseId, "chapterId", chapterId, "questions", questions);
        } catch (Exception e) {
            log.error("[after_class_test_generate_skill] error", e);
            return Map.of("error", "生成失败: " + e.getMessage());
        }
    }

    // ---------- 功能二：运维 Skill ----------

    /**
     * service_health_check_skill：Nacos 服务实例健康状态
     */
    public Object serviceHealthCheckSkill(String serviceName) {
        try {
            String url = "http://" + nacosServerAddr + "/nacos/v1/ns/instance/list?serviceName=" + (serviceName != null && !serviceName.isBlank() ? serviceName : "");
            if (serviceName == null || serviceName.isBlank()) {
                url = "http://" + nacosServerAddr + "/nacos/v1/ns/service/list?pageNo=1&pageSize=100";
            }
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return objectMapper.readValue(resp.getBody(), Map.class);
            }
            return Map.of("error", "Nacos 返回非 200");
        } catch (Exception e) {
            log.error("[service_health_check_skill] error", e);
            return Map.of("error", "查询失败: " + e.getMessage());
        }
    }

    /**
     * gateway_interface_monitor_skill：网关接口监控（占位，需 Gateway 暴露 metrics）
     */
    public Object gatewayInterfaceMonitorSkill(String timeRange, String serviceName) {
        return Map.of(
                "message", "需在 Spring Cloud Gateway 开启 actuator 并暴露 metrics 后对接",
                "timeRange", timeRange != null ? timeRange : "",
                "serviceName", serviceName != null ? serviceName : ""
        );
    }

    /**
     * middleware_status_check_skill：Redis / RocketMQ 状态
     */
    @SuppressWarnings("deprecation")
    public Object middlewareStatusCheckSkill(String middlewareType) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            if (middlewareType == null || middlewareType.isBlank() || "redis".equalsIgnoreCase(middlewareType)) {
                try {
                    if (stringRedisTemplate != null) {
                        var conn = stringRedisTemplate.getConnectionFactory().getConnection();
                        Object info = conn.info("memory");
                        String infoStr = info != null ? info.toString() : "";
                        result.put("redis", Map.of("status", "ok", "info", infoStr));
                    } else {
                        result.put("redis", Map.of("status", "未注入 Redis", "host", redisHost));
                    }
                } catch (Exception e) {
                    result.put("redis", Map.of("status", "error", "message", e.getMessage()));
                }
            }
            if (middlewareType == null || middlewareType.isBlank() || "rocketmq".equalsIgnoreCase(middlewareType)) {
                result.put("rocketmq", Map.of("status", "需对接 RocketMQ Dashboard Open API 或 MQAdmin 获取堆积与死信队列"));
            }
            if (result.isEmpty()) result.put("message", "未指定或未实现 middleware_type");
            return result;
        } catch (Exception e) {
            log.error("[middleware_status_check_skill] error", e);
            return Map.of("error", e.getMessage());
        }
    }

    /**
     * exception_log_query_skill：异常日志查询（占位，可对接日志文件或 ELK）
     */
    public Object exceptionLogQuerySkill(String timeRange, String serviceName, String keyword) {
        return Map.of(
                "message", "未配置日志检索（可对接项目日志文件或 ELK）",
                "timeRange", timeRange != null ? timeRange : "",
                "serviceName", serviceName != null ? serviceName : "",
                "keyword", keyword != null ? keyword : ""
        );
    }

    private Long resolveUserId(String username) {
        if (username == null || username.isBlank()) return null;
        try {
            R<Object> r = userServiceClient.getByUsername(username.trim());
            if (r == null || !Boolean.TRUE.equals(r.isSuccess()) || r.getData() == null) return null;
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) r.getData();
            Object id = user.get("id");
            if (id instanceof Number) return ((Number) id).longValue();
            if (id != null) return Long.parseLong(id.toString());
        } catch (Exception e) {
            log.warn("resolveUserId failed for username={}", username, e);
        }
        return null;
    }
}
