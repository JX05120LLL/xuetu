package com.star.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户已购课程摘要 DTO（与 order-service UserCourse 返回字段兼容）
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCourseSummaryDTO implements Serializable {
    private Long courseId;
    private LocalDateTime purchaseTime;
    private LocalDateTime expireTime;
    private Integer progress;
    private LocalDateTime lastLearnTime;
}
