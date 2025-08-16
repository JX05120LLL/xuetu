package com.star.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户课程关系实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_course")
@Schema(description = "用户课程关系信息")
public class UserCourse extends BaseEntity {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @Schema(description = "购买价格", example = "179.10")
    private BigDecimal purchasePrice;

    @Schema(description = "购买时间")
    private LocalDateTime purchaseTime;

    @Schema(description = "过期时间（永久课程为NULL）")
    private LocalDateTime expireTime;

    @Schema(description = "状态(1:正常,2:已过期,3:已退款)", example = "1")
    private Integer status;

    @Schema(description = "学习进度百分比(0-100)", example = "15")
    private Integer progress;

    @Schema(description = "最后学习时间")
    private LocalDateTime lastLearnTime;

    // 枚举定义
    public enum Status {
        NORMAL(1, "正常"),
        EXPIRED(2, "已过期"),
        REFUNDED(3, "已退款");

        private final Integer value;
        private final String label;

        Status(Integer value, String label) {
            this.value = value;
            this.label = label;
        }

        public Integer getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }
}