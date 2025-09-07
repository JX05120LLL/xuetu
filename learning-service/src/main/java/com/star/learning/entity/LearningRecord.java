package com.star.learning.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 学习记录实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("learning_record")
public class LearningRecord extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课时ID
     */
    private Long lessonId;

    /**
     * 学习进度(秒)
     */
    private Integer progress;

    /**
     * 是否完成(0:否,1:是)
     */
    private Integer isCompleted;

    /**
     * 最后学习时间
     */
    private LocalDateTime lastLearnTime;

    /**
     * 学习完成状态枚举
     */
    public enum CompletionStatus {
        NOT_COMPLETED(0, "未完成"),
        COMPLETED(1, "已完成");

        private final Integer value;
        private final String label;

        CompletionStatus(Integer value, String label) {
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