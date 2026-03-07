package com.star.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 知识库入库结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识库入库结果")
public class KnowledgeIngestResult {

    @Schema(description = "入库类型", example = "course")
    private String type;

    @Schema(description = "成功入库的文档数量", example = "42")
    private int count;

    @Schema(description = "结果说明", example = "课程知识入库完成")
    private String message;
}
