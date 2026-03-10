package com.star.ai.skill;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * /skill/rag/search 接口的请求体
 */
@Data
public class RagSearchRequest {

    /** 用户问题或检索关键词 */
    @NotBlank(message = "query 不能为空")
    private String query;

    /** 返回最相关的 Top-K 文档数，默认 5 */
    private Integer topK = 5;
}
