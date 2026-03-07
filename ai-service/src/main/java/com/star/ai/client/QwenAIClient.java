package com.star.ai.client;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.star.ai.exception.AIServiceException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 阿里通义千问AI客户端
 * 
 * @author star
 */
@Slf4j
@Component
public class QwenAIClient {

    @Value("${aliyun.ai.api-key}")
    private String apiKey;

    @Value("${aliyun.ai.api-url}")
    private String apiUrl;

    @Value("${aliyun.ai.model:qwen-turbo}")
    private String model;

    @Value("${aliyun.ai.max-tokens:2000}")
    private Integer maxTokens;

    @Value("${aliyun.ai.temperature:0.8}")
    private Double temperature;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    /**
     * 发送问题给AI并获取回答
     * 
     * @param question 用户问题
     * @param conversationId 对话ID(用于上下文)
     * @return AI回答
     */
    public String chat(String question, String conversationId) {
        try {
            log.info("调用通义千问AI, 问题: {}, 对话ID: {}", question, conversationId);

            // 1. 构建消息列表（包含历史对话）
            List<Message> messages = buildMessages(question, conversationId);

            // 2. 构建请求体
            JSONObject requestBody = buildRequestBody(messages);

            // 3. 发送请求
            String responseText = sendRequest(requestBody);

            // 4. 解析响应
            JSONObject responseJson = JSONUtil.parseObj(responseText);
            
            // 检查是否有错误
            if (responseJson.containsKey("code")) {
                String errorCode = responseJson.getStr("code");
                String errorMsg = responseJson.getStr("message");
                log.error("通义千问API返回错误: code={}, message={}", errorCode, errorMsg);
                throw new AIServiceException("AI服务调用失败: " + errorMsg);
            }

            // 提取AI回答
            String answer = extractAnswer(responseJson);

            // 5. 保存对话到Redis（用于上下文）
            if (conversationId != null) {
                saveConversationHistory(conversationId, question, answer);
            }

            log.info("通义千问AI回答成功, 答案长度: {}", answer.length());
            return answer;

        } catch (Exception e) {
            log.error("调用通义千问AI失败", e);
            throw new AIServiceException("AI服务暂时不可用，请稍后重试: " + e.getMessage());
        }
    }

    /**
     * 构建消息列表
     */
    private List<Message> buildMessages(String question, String conversationId) {
        List<Message> messages = new ArrayList<>();

        // 添加系统提示（可选）
        messages.add(new Message("system", 
            "你是学途教育平台的AI学习助手，名字叫小途。你的任务是帮助学生解答学习中的问题，" +
            "提供学习建议和课程推荐。回答要友好、专业、通俗易懂。"));

        // 从Redis获取历史对话（如果有）
        if (conversationId != null) {
            List<Message> history = getConversationHistory(conversationId);
            if (history != null && !history.isEmpty()) {
                messages.addAll(history);
            }
        }

        // 添加当前问题
        messages.add(new Message("user", question));

        return messages;
    }

    /**
     * 构建请求体
     */
    private JSONObject buildRequestBody(List<Message> messages) {
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", model);
        
        // 构建messages数组
        JSONArray messagesArray = new JSONArray();
        for (Message msg : messages) {
            JSONObject msgObj = new JSONObject();
            msgObj.set("role", msg.getRole());
            msgObj.set("content", msg.getContent());
            messagesArray.add(msgObj);
        }
        requestBody.set("input", new JSONObject().set("messages", messagesArray));

        // 参数配置
        JSONObject parameters = new JSONObject();
        parameters.set("max_tokens", maxTokens);
        parameters.set("temperature", temperature);
        parameters.set("result_format", "message");  // 返回消息格式
        requestBody.set("parameters", parameters);

        return requestBody;
    }

    /**
     * 发送HTTP请求
     */
    private String sendRequest(JSONObject requestBody) throws IOException {
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败: " + response.code() + " " + response.message());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("响应体为空");
            }

            return responseBody.string();
        }
    }

    /**
     * 提取AI回答
     */
    private String extractAnswer(JSONObject responseJson) {
        try {
            JSONObject output = responseJson.getJSONObject("output");
            if (output == null) {
                throw new AIServiceException("响应格式错误：缺少output字段");
            }

            JSONArray choices = output.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new AIServiceException("响应格式错误：缺少choices字段");
            }

            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            String content = message.getStr("content");

            if (content == null || content.trim().isEmpty()) {
                throw new AIServiceException("AI返回内容为空");
            }

            return content.trim();

        } catch (Exception e) {
            log.error("解析AI响应失败: {}", responseJson, e);
            throw new AIServiceException("解析AI响应失败: " + e.getMessage());
        }
    }

    /**
     * 从Redis获取对话历史
     */
    @SuppressWarnings("unchecked")
    private List<Message> getConversationHistory(String conversationId) {
        try {
            String key = "ai:conversation:" + conversationId;
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj instanceof List) {
                return (List<Message>) obj;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.warn("获取对话历史失败: conversationId={}", conversationId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 保存对话历史到Redis
     */
    private void saveConversationHistory(String conversationId, String question, String answer) {
        try {
            String key = "ai:conversation:" + conversationId;
            List<Message> history = getConversationHistory(conversationId);

            // 添加新的对话
            history.add(new Message("user", question));
            history.add(new Message("assistant", answer));

            // 限制历史记录数量（最多保留最近10轮对话，即20条消息）
            if (history.size() > 20) {
                history = history.subList(history.size() - 20, history.size());
            }

            // 保存到Redis，设置过期时间30分钟
            redisTemplate.opsForValue().set(key, history, 30, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.warn("保存对话历史失败: conversationId={}", conversationId, e);
        }
    }

    /**
     * 清空对话历史
     */
    public void clearConversation(String conversationId) {
        try {
            String key = "ai:conversation:" + conversationId;
            redisTemplate.delete(key);
            log.info("清空对话历史成功: conversationId={}", conversationId);
        } catch (Exception e) {
            log.warn("清空对话历史失败: conversationId={}", conversationId, e);
        }
    }

    /**
     * 消息类（内部使用）
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class Message implements java.io.Serializable {
        private String role;    // system/user/assistant
        private String content;
    }
}
