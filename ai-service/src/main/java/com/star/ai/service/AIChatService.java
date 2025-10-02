package com.star.ai.service;

import com.star.ai.dto.ChatHistoryDTO;
import com.star.ai.dto.ChatRequest;
import com.star.ai.dto.ChatResponse;

import java.util.List;

/**
 * AI聊天服务接口
 * 
 * @author star
 */
public interface AIChatService {

    /**
     * AI问答
     * 
     * @param userId 用户ID
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponse chat(Long userId, ChatRequest request);

    /**
     * 获取对话历史
     * 
     * @param userId 用户ID
     * @param conversationId 对话ID
     * @param limit 限制条数
     * @return 对话历史列表
     */
    List<ChatHistoryDTO> getChatHistory(Long userId, String conversationId, Integer limit);

    /**
     * 清空对话
     * 
     * @param userId 用户ID
     * @param conversationId 对话ID
     * @return 是否成功
     */
    Boolean clearConversation(Long userId, String conversationId);

    /**
     * 获取用户的对话列表
     * 
     * @param userId 用户ID
     * @return 对话ID列表
     */
    List<String> getUserConversations(Long userId);
}
