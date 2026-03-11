package com.star.ai.openclaw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenClaw 技能接口的请求体示例（仅用于 Knife4j 文档展示与测试预填，实际仍从 HttpServletRequest 取参）
 */
public class SkillRequestExamples {

    @Schema(description = "课程查询技能请求示例")
    @Data
    public static class CourseQuery {
        @Schema(description = "用户名", example = "testuser1", requiredMode = Schema.RequiredMode.REQUIRED)
        private String username;
        @Schema(description = "课程ID，选填，不填查全部", example = "1")
        private Long courseId;
    }

    @Schema(description = "学习进度跟踪技能请求示例")
    @Data
    public static class LearningProgressTrack {
        @Schema(description = "用户名", example = "testuser1", requiredMode = Schema.RequiredMode.REQUIRED)
        private String username;
        @Schema(description = "课程ID，选填", example = "1")
        private Long courseId;
    }

    @Schema(description = "学习计划生成技能请求示例")
    @Data
    public static class LearningPlanGenerate {
        @Schema(description = "用户名", example = "testuser1", requiredMode = Schema.RequiredMode.REQUIRED)
        private String username;
        @Schema(description = "课程ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long courseId;
        @Schema(description = "每日学习小时数", example = "1.5", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double daily_study_hours;
        @Schema(description = "目标完成日期 yyyy-MM-dd", example = "2025-06-30", requiredMode = Schema.RequiredMode.REQUIRED)
        private String target_finish_date;
    }

    @Schema(description = "课程知识库问答技能请求示例")
    @Data
    public static class CourseRagQa {
        @Schema(description = "用户问题", example = "什么是Spring Boot？", requiredMode = Schema.RequiredMode.REQUIRED)
        private String user_question;
        @Schema(description = "用户名，选填", example = "testuser1")
        private String username;
        @Schema(description = "课程ID，选填", example = "1")
        private Long courseId;
    }

    @Schema(description = "学习提醒技能请求示例")
    @Data
    public static class LearningRemind {
        @Schema(description = "用户名", example = "testuser1", requiredMode = Schema.RequiredMode.REQUIRED)
        private String username;
        @Schema(description = "提醒时间", example = "21:00", requiredMode = Schema.RequiredMode.REQUIRED)
        private String remind_time;
        @Schema(description = "关联课程ID，选填", example = "1")
        private Long course_id;
    }

    @Schema(description = "课后测验生成技能请求示例")
    @Data
    public static class AfterClassTestGenerate {
        @Schema(description = "课程ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long course_id;
        @Schema(description = "章节ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long chapter_id;
        @Schema(description = "题目数量，默认5", example = "5")
        private Integer question_count;
    }

    @Schema(description = "微服务健康检查技能请求示例")
    @Data
    public static class ServiceHealthCheck {
        @Schema(description = "服务名，不填则查全部", example = "user-service")
        private String service_name;
    }

    @Schema(description = "网关接口监控技能请求示例")
    @Data
    public static class GatewayInterfaceMonitor {
        @Schema(description = "时间范围", example = "1h")
        private String time_range;
        @Schema(description = "服务名", example = "ai-service")
        private String service_name;
    }

    @Schema(description = "中间件状态排查技能请求示例")
    @Data
    public static class MiddlewareStatusCheck {
        @Schema(description = "中间件类型 redis/rocketmq，不填查全部", example = "redis")
        private String middleware_type;
    }

    @Schema(description = "异常日志查询技能请求示例")
    @Data
    public static class ExceptionLogQuery {
        @Schema(description = "时间范围", example = "1h")
        private String time_range;
        @Schema(description = "服务名", example = "ai-service")
        private String service_name;
        @Schema(description = "关键词", example = "Exception")
        private String keyword;
    }
}
