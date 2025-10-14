package com.star.learning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户课程DTO（包含课程详细信息）
 * 
 * @author star
 */
@Data
@Schema(description = "用户课程信息（含课程详情）")
public class UserCourseDTO {

    @Schema(description = "用户课程ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程封面")
    private String coverImage;

    @Schema(description = "课程描述")
    private String courseDescription;

    @Schema(description = "讲师ID")
    private Long teacherId;

    @Schema(description = "讲师名称")
    private String teacherName;

    @Schema(description = "课程总时长（分钟）")
    private Integer totalDuration;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "购买价格")
    private BigDecimal purchasePrice;

    @Schema(description = "购买时间")
    private LocalDateTime purchaseTime;

    @Schema(description = "学习进度(0-100)")
    private Integer progress;

    @Schema(description = "学习时长（秒）", example = "3600")
    private Integer studyDuration;

    @Schema(description = "最后学习时间")
    private LocalDateTime lastStudyTime;

    @Schema(description = "状态(1:正常,2:过期,3:禁用)")
    private Integer status;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}