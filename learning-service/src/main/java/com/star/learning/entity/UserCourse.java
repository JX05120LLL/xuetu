package com.star.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
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
public class UserCourse extends BaseEntity {

    /**
     * 用户课程ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 购买价格
     */
    private BigDecimal purchasePrice;

    /**
     * 购买时间
     */
    private LocalDateTime purchaseTime;

    /**
     * 学习进度(0-100)
     */
    private Integer progress;

    /**
     * 最后学习时间
     */
    private LocalDateTime lastLearnTime;

    /**
     * 状态(0:未开通,1:正常,2:过期,3:禁用)
     */
    private Integer status;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 课程状态枚举
     */
    public enum Status {
        INACTIVE(0, "未开通"),
        ACTIVE(1, "正常"),
        EXPIRED(2, "过期"),
        DISABLED(3, "禁用");

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