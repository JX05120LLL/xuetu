package com.star.ai.controller;

import com.star.ai.dto.ChatHistoryDTO;
import com.star.ai.dto.ChatRequest;
import com.star.ai.dto.ChatResponse;
import com.star.ai.service.AIChatService;
import com.star.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * AI聊天控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "AI聊天", description = "AI智能问答接口")
public class AIChatController {

    private final AIChatService aiChatService;

    /**
     * AI问答
     */
    @PostMapping("/ask")
    @Operation(summary = "AI问答", description = "向AI提问并获得智能回答")
    public R<ChatResponse> ask(@Valid @RequestBody ChatRequest request,
                               HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        log.info("用户{}提问AI: {}", userId, request.getQuestion());
        
        ChatResponse response = aiChatService.chat(userId, request);
        return R.ok(response, "AI回答成功");
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/history")
    @Operation(summary = "对话历史", description = "获取用户的AI对话历史")
    public R<List<ChatHistoryDTO>> getHistory(
            @Parameter(description = "对话ID(可选，查询特定对话)") @RequestParam(required = false) String conversationId,
            @Parameter(description = "限制条数") @RequestParam(defaultValue = "50") Integer limit,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        List<ChatHistoryDTO> history = aiChatService.getChatHistory(userId, conversationId, limit);
        return R.ok(history);
    }

    /**
     * 清空对话
     */
    @DeleteMapping("/clear")
    @Operation(summary = "清空对话", description = "清空当前对话上下文")
    public R<Boolean> clearConversation(
            @Parameter(description = "对话ID", required = true) @RequestParam String conversationId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean result = aiChatService.clearConversation(userId, conversationId);
        return R.ok(result, "对话已清空");
    }

    /**
     * 获取最近对话列表
     */
    @GetMapping("/conversations")
    @Operation(summary = "对话列表", description = "获取用户最近的对话列表")
    public R<List<String>> getConversations(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        List<String> conversations = aiChatService.getUserConversations(userId);
        return R.ok(conversations);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查AI服务是否正常")
    public R<String> health() {
        return R.ok("AI服务运行正常 - 通义千问");
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("无效的用户ID: {}", userIdHeader);
            }
        }
        // 开发阶段返回默认用户ID
        log.warn("未能从请求中获取用户ID，使用默认值");
        return 1L;
    }
}
