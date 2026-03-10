package com.star.ai.skill;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * /skill/rag/search 接口的响应体
 */
@Data
@Builder
public class RagSearchResponse {

    /** 是否命中知识库（results 不为空时为 true） */
    private boolean hit;

    /** 检索结果列表 */
    private List<ResultItem> results;

    @Data
    @Builder
    public static class ResultItem {
        /** 文档正文 */
        private String text;
        /** 文档元数据（type、source、courseId 等） */
        private Map<String, Object> metadata;
    }
}
