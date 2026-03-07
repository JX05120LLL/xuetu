package com.star.ai.controller;

import com.star.ai.dto.KnowledgeIngestResult;
import com.star.ai.service.KnowledgeService;
import com.star.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库管理接口
 *
 * 用于将课程、FAQ 等数据向量化并写入 Qdrant。
 * 本接口为管理员手动触发操作，不对普通用户开放。
 */
@Slf4j
@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
@Tag(name = "知识库管理", description = "RAG 知识库入库与维护")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    /**
     * 全量入库（课程 + FAQ）
     */
    @PostMapping("/ingest/all")
    @Operation(summary = "全量入库", description = "将所有课程和 FAQ 向量化写入 Qdrant 知识库")
    public R<List<KnowledgeIngestResult>> ingestAll() {
        log.info("[知识库] 触发全量入库");
        List<KnowledgeIngestResult> results = knowledgeService.ingestAll();
        return R.ok(results, "全量入库完成");
    }

    /**
     * 仅入库课程
     */
    @PostMapping("/ingest/courses")
    @Operation(summary = "入库课程", description = "将所有课程向量化写入 Qdrant")
    public R<KnowledgeIngestResult> ingestCourses() {
        log.info("[知识库] 触发课程入库");
        return R.ok(knowledgeService.ingestAllCourses());
    }

    /**
     * 仅入库 FAQ
     */
    @PostMapping("/ingest/faqs")
    @Operation(summary = "入库 FAQ", description = "将所有常见问题向量化写入 Qdrant")
    public R<KnowledgeIngestResult> ingestFaqs() {
        log.info("[知识库] 触发 FAQ 入库");
        return R.ok(knowledgeService.ingestAllFaqs());
    }

    /**
     * 增量更新单门课程
     */
    @PostMapping("/ingest/course/{courseId}")
    @Operation(summary = "增量更新课程", description = "删除旧向量并重新入库指定课程")
    public R<KnowledgeIngestResult> ingestSingleCourse(
            @Parameter(description = "课程 ID") @PathVariable Long courseId) {
        log.info("[知识库] 触发单课程增量入库, courseId={}", courseId);
        return R.ok(knowledgeService.ingestSingleCourse(courseId));
    }

    /**
     * 清空指定类型的向量文档
     */
    @DeleteMapping("/clear/{type}")
    @Operation(summary = "清空知识", description = "清空指定类型的向量文档，type 可为 course 或 faq")
    public R<String> clearByType(
            @Parameter(description = "文档类型：course / faq") @PathVariable String type) {
        log.info("[知识库] 触发清空操作, type={}", type);
        knowledgeService.deleteByType(type);
        return R.ok("已清空 " + type + " 类型的向量文档");
    }
}
