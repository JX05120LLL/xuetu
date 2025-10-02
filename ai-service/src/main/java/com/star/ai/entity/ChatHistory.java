package com.star.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史实体
 * 
 * @author star
 */
@Data
@TableName("chat_history")
public class ChatHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 对话会话ID
     */
    private String conversationId;

    /**
     * 角色: user/assistant
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 关联课程ID
     */
    private Long courseId;

    /**
     * 关联课时ID
     */
    private Long lessonId;

    /**
     * 消耗的tokens数
     */
    private Integer tokens;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
