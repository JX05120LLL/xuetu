package com.star.learning.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学习记录实体类
 * 使用复合主键 (user_id, lesson_id)
 * 
 * @author star
 */
@Data
@TableName("learning_record")
public class LearningRecord implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 上次播放位置(秒)
     */
    private Integer lastPosition;

    /**
     * 是否完成(0:否,1:是)
     */
    private Integer isCompleted;

    /**
     * 最后学习时间
     */
    private LocalDateTime lastLearnTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

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