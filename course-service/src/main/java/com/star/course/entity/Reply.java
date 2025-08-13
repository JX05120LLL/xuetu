package com.star.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 回复实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reply")
public class Reply extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 回复用户ID
     */
    private Long userId;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 回复目标用户ID
     */
    private Long targetUserId;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 状态(0:隐藏,1:正常)
     */
    private Integer status;

    /**
     * 状态枚举
     */
    public enum Status {
        HIDDEN(0, "隐藏"),
        NORMAL(1, "正常");

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