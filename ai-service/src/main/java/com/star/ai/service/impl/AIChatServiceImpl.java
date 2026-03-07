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
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 聊天服务实现
 *
 * 提供两种问答模式：
 *   1. 普通模式（useRag=false）：沿用 QwenAIClient 的 OkHttp 链路，带多轮上下文
 *   2. RAG 模式（useRag=true）：先检索 Qdrant 向量库，将相关知识片段注入 Prompt，
 *      再通过 Spring AI ChatModel 调用 LLM 生成带引用的回答
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIChatServiceImpl implements AIChatService {

    // ─── 普通模式依赖 ──────────────────────────────────────────────────────
    private final QwenAIClient qwenAIClient;

    // ─── RAG 模式依赖（Spring AI 自动装配）────────────────────────────────
    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    private final ChatHistoryMapper chatHistoryMapper;

    /** RAG 模式下检索的 Top-K 文档数 */
    private static final int RAG_TOP_K = 5;

    /** RAG 系统 Prompt */
    private static final String RAG_SYSTEM_PROMPT =
            "你是学途在线教育平台的智能助手「小途」。\n" +
            "请优先基于下方「知识库参考内容」来回答用户问题，若知识库内容不足，" +
            "可结合自身知识补充，但需在回答末尾注明哪些内容来自知识库。\n" +
            "回答要友好、专业、通俗易懂，使用中文回答。";

    // ─── 公开接口实现 ──────────────────────────────────────────────────────

    @Override
    public ChatResponse chat(Long userId, ChatRequest request) {
        log.info("用户 {} 发起 AI 对话，问题: {}", userId, request.getQuestion());

        // 生成或复用对话 ID
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = "conv-" + IdUtil.simpleUUID();
        }

        // 始终优先走 RAG：先查知识库，有结果用知识库回答，没有则降级普通 AI 回答
        return ragChat(userId, conversationId, request);
    }

    @Override
    public List<ChatHistoryDTO> getChatHistory(Long userId, String conversationId, Integer limit) {
        LambdaQueryWrapper<ChatHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatHistory::getUserId, userId);

        if (conversationId != null && !conversationId.isBlank()) {
            queryWrapper.eq(ChatHistory::getConversationId, conversationId);
        }

        queryWrapper.orderByDesc(ChatHistory::getCreatedTime);
        queryWrapper.last("LIMIT " + Math.min(limit, 200));

        List<ChatHistory> historyList = chatHistoryMapper.selectList(queryWrapper);
        return historyList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Boolean clearConversation(Long userId, String conversationId) {
        qwenAIClient.clearConversation(conversationId);
        log.info("清空对话成功: userId={}, conversationId={}", userId, conversationId);
        return true;
    }

    @Override
    public List<String> getUserConversations(Long userId) {
        return chatHistoryMapper.selectUserConversations(userId);
    }

    // ─── 普通模式（原有 OkHttp 链路）─────────────────────────────────────

    private ChatResponse normalChat(Long userId, String conversationId, ChatRequest request) {
        String answer = qwenAIClient.chat(request.getQuestion(), conversationId);

        saveChatHistory(userId, conversationId, "user", request.getQuestion(),
                request.getCourseId(), request.getLessonId());
        saveChatHistory(userId, conversationId, "assistant", answer,
                request.getCourseId(), request.getLessonId());

        return ChatResponse.builder()
                .answer(answer)
                .conversationId(conversationId)
                .question(request.getQuestion())
                .answerTime(LocalDateTime.now())
                .build();
    }

    // ─── RAG 模式（向量检索 + Spring AI ChatModel）────────────────────────

    /**
     * RAG 问答核心流程：
     *
     *   1. 向 Qdrant 检索与用户问题最相关的 Top-K 文档片段
     *   2. 若知识库无相关内容，降级为普通 AI 回答
     *   3. 将检索结果拼接为上下文，构造增强 Prompt
     *   4. 通过 Spring AI ChatModel（DashScope qwen-turbo）生成回答
     *   5. 从文档 Metadata 中提取来源，附加到响应中
     */
    private ChatResponse ragChat(Long userId, String conversationId, ChatRequest request) {
        String question = request.getQuestion();

        // ① 向量检索
        List<Document> docs;
        try {
            docs = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(question)
                            .topK(RAG_TOP_K)
                            .build()
            );
            log.info("[RAG] 检索完成，共找到 {} 条相关文档", docs.size());
        } catch (Exception e) {
            log.error("[RAG] 向量检索失败，降级为普通问答: {}", e.getMessage());
            return normalChat(userId, conversationId, request);
        }

        // ② 知识库无相关内容，降级为普通 AI 回答
        if (docs.isEmpty()) {
            log.info("[RAG] 知识库未找到相关内容，降级为普通 AI 回答");
            return normalChat(userId, conversationId, request);
        }

        // ③ 构建增强 Prompt
        String augmentedUserMessage = buildAugmentedUserMessage(question, docs);

        // ④ 调用 LLM（通过 Spring AI ChatModel）
        String answer;
        try {
            SystemMessage systemMessage = new SystemMessage(RAG_SYSTEM_PROMPT);
            UserMessage userMessage = new UserMessage(augmentedUserMessage);
            org.springframework.ai.chat.model.ChatResponse aiResponse =
                    chatModel.call(new Prompt(List.of(systemMessage, userMessage)));
            answer = aiResponse.getResult().getOutput().getText();
            log.info("[RAG] LLM 回答成功，答案长度: {}", answer.length());
        } catch (Exception e) {
            log.error("[RAG] LLM 调用失败: {}", e.getMessage());
            throw new RuntimeException("AI 服务暂时不可用，请稍后重试");
        }

        // ⑤ 提取知识来源（去重 + 过滤空值）
        List<String> sources = docs.stream()
                .map(d -> (String) d.getMetadata().getOrDefault("source", ""))
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());

        // ⑥ 保存对话记录
        saveChatHistory(userId, conversationId, "user", question,
                request.getCourseId(), request.getLessonId());
        saveChatHistory(userId, conversationId, "assistant", answer,
                request.getCourseId(), request.getLessonId());

        return ChatResponse.builder()
                .answer(answer)
                .conversationId(conversationId)
                .question(question)
                .answerTime(LocalDateTime.now())
                .sources(sources)
                .build();
    }

    /**
     * 将检索到的知识片段与用户问题合并，构造增强的用户消息。
     * 若未检索到相关内容，则直接返回原始问题（不拼接上下文）。
     */
    private String buildAugmentedUserMessage(String question, List<Document> docs) {
        if (docs.isEmpty()) {
            log.info("[RAG] 知识库中未找到相关内容，将由 LLM 直接回答");
            return question;
        }

        String context = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        return "[知识库参考内容]\n" + context + "\n\n[用户问题]\n" + question;
    }

    // ─── 私有辅助方法 ──────────────────────────────────────────────────────

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

    private ChatHistoryDTO convertToDTO(ChatHistory chatHistory) {
        ChatHistoryDTO dto = new ChatHistoryDTO();
        BeanUtils.copyProperties(chatHistory, dto);
        return dto;
    }
}
