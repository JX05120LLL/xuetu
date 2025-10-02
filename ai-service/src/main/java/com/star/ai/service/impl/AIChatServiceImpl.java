package com.star.ai.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.ai.client.QwenAIClient;
import com.star.ai.dto.ChatHistoryDTO;
import com.star.ai.dto.ChatRequest;
import com.star.ai.dto.ChatResponse;
import com.star.ai.entity.ChatHistory;
import com.star.ai.mapper.ChatHistoryMapper;
import com.star.ai.service.AIChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI聊天服务实现
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIChatServiceImpl implements AIChatService {

    private final QwenAIClient qwenAIClient;
    private final ChatHistoryMapper chatHistoryMapper;

    @Override
    public ChatResponse chat(Long userId, ChatRequest request) {
        log.info("用户{}发起AI对话, 问题: {}", userId, request.getQuestion());

        // 1. 生成对话ID（如果没有）
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.trim().isEmpty()) {
            conversationId = "conv-" + IdUtil.simpleUUID();
        }

        // 2. 调用AI获取回答
        String answer = qwenAIClient.chat(request.getQuestion(), conversationId);

        // 3. 保存用户问题到数据库
        saveChatHistory(userId, conversationId, "user", request.getQuestion(), 
                        request.getCourseId(), request.getLessonId());

        // 4. 保存AI回答到数据库
        saveChatHistory(userId, conversationId, "assistant", answer, 
                        request.getCourseId(), request.getLessonId());

        // 5. 构建响应
        return ChatResponse.builder()
                .answer(answer)
                .conversationId(conversationId)
                .question(request.getQuestion())
                .answerTime(LocalDateTime.now())
                .build();
    }

    @Override
    public List<ChatHistoryDTO> getChatHistory(Long userId, String conversationId, Integer limit) {
        LambdaQueryWrapper<ChatHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatHistory::getUserId, userId);
        
        if (conversationId != null && !conversationId.trim().isEmpty()) {
            queryWrapper.eq(ChatHistory::getConversationId, conversationId);
        }
        
        queryWrapper.orderByDesc(ChatHistory::getCreatedTime);
        queryWrapper.last("LIMIT " + Math.min(limit, 200)); // 最多返回200条

        List<ChatHistory> historyList = chatHistoryMapper.selectList(queryWrapper);

        return historyList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Boolean clearConversation(Long userId, String conversationId) {
        // 1. 清空Redis中的对话上下文
        qwenAIClient.clearConversation(conversationId);

        // 2. 从数据库删除对话历史（可选，建议保留）
        // LambdaQueryWrapper<ChatHistory> queryWrapper = new LambdaQueryWrapper<>();
        // queryWrapper.eq(ChatHistory::getUserId, userId);
        // queryWrapper.eq(ChatHistory::getConversationId, conversationId);
        // chatHistoryMapper.delete(queryWrapper);

        log.info("清空对话成功: userId={}, conversationId={}", userId, conversationId);
        return true;
    }

    @Override
    public List<String> getUserConversations(Long userId) {
        return chatHistoryMapper.selectUserConversations(userId);
    }

    /**
     * 保存对话历史
     */
    private void saveChatHistory(Long userId, String conversationId, String role, 
                                 String content, Long courseId, Long lessonId) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setUserId(userId);
        chatHistory.setConversationId(conversationId);
        chatHistory.setRole(role);
        chatHistory.setContent(content);
        chatHistory.setCourseId(courseId);
        chatHistory.setLessonId(lessonId);
        chatHistory.setModel("qwen-turbo");
        chatHistory.setCreatedTime(LocalDateTime.now());

        chatHistoryMapper.insert(chatHistory);
    }

    /**
     * 转换为DTO
     */
    private ChatHistoryDTO convertToDTO(ChatHistory chatHistory) {
        ChatHistoryDTO dto = new ChatHistoryDTO();
        BeanUtils.copyProperties(chatHistory, dto);
        return dto;
    }
}
