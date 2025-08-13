package com.star.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论点赞实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment_like")
public class CommentLike extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 评论ID或回复ID
     */
    private Long commentId;

    /**
     * 类型(1:评论点赞,2:回复点赞)
     */
    private Integer type;

    /**
     * 点赞类型枚举
     */
    public enum Type {
        COMMENT(1, "评论点赞"),
        REPLY(2, "回复点赞");

        private final Integer value;
        private final String label;

        Type(Integer value, String label) {
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