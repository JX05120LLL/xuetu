package com.star.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 常见问题实体（对应 ai_faq 表）
 */
@Data
@TableName("ai_faq")
public class AiFaq implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类：学习方法 / 编程问题 / 课程相关 */
    private String category;

    /** 问题 */
    private String question;

    /** 答案 */
    private String answer;

    /** 被使用次数 */
    private Integer useCount;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
