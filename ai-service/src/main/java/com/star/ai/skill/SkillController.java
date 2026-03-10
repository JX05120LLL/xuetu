package com.star.ai.skill;

import com.star.ai.dto.CourseDTO;
import com.star.ai.feign.CourseServiceClient;
import com.star.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 技能接口层 —— 将学途已有能力封装为标准 HTTP 技能接口
 *
 * 这些接口即 Nacos skill-registry.json 中 invokeType=java_http 技能的实际 invokeUrl 对应实现。
 * 编排层（或外部调用方）通过 POST 这两个接口即可使用学途本地能力，无需了解内部实现细节。
 */
@Slf4j
@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
@Tag(name = "OpenClaw 技能接口", description = "学途自有技能的 HTTP 调用入口，供技能编排层使用")
public class SkillController {

    private final VectorStore vectorStore;
    private final CourseServiceClient courseServiceClient;

    /** 难度中文名 -> 数字映射，与 CourseServiceClient.getCourseList 的 level 参数对齐 */
    private static final Map<String, Integer> LEVEL_MAP = Map.of(
            "初级", 1, "中级", 2, "高级", 3
    );

    // ── 技能1：RAG 知识库检索 ─────────────────────────────────────────────

    /**
     * RAG 语义检索技能
     *
     * 对应 Nacos 注册：id=xuetu-rag，invokeUrl=http://ai-service:8066/skill/rag/search
     */
    @PostMapping("/rag/search")
    @Operation(summary = "RAG 知识库检索", description = "基于向量语义检索课程与 FAQ 知识库，返回最相关的文档片段")
    public R<RagSearchResponse> ragSearch(@Valid @RequestBody RagSearchRequest request) {
        log.info("[Skill-RAG] 收到检索请求，query={}, topK={}", request.getQuery(), request.getTopK());

        List<Document> docs;
        try {
            docs = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(request.getQuery())
                            .topK(request.getTopK())
                            .build()
            );
        } catch (Exception e) {
            log.error("[Skill-RAG] 向量检索失败: {}", e.getMessage());
            return R.error("知识库检索失败：" + e.getMessage());
        }

        if (docs.isEmpty()) {
            log.info("[Skill-RAG] 未检索到相关文档");
            return R.ok(RagSearchResponse.builder()
                    .hit(false)
                    .results(List.of())
                    .build(), "知识库中无相关内容");
        }

        List<RagSearchResponse.ResultItem> items = docs.stream()
                .map(doc -> RagSearchResponse.ResultItem.builder()
                        .text(doc.getText())
                        .metadata(doc.getMetadata())
                        .build())
                .collect(Collectors.toList());

        log.info("[Skill-RAG] 检索完成，命中 {} 条文档", items.size());
        return R.ok(RagSearchResponse.builder()
                .hit(true)
                .results(items)
                .build(), "检索成功");
    }

    // ── 技能2：课程推荐列表 ───────────────────────────────────────────────

    /**
     * 课程推荐/筛选技能
     *
     * 对应 Nacos 注册：id=xuetu-course-advisor，invokeUrl=http://ai-service:8066/skill/course/list
     */
    @PostMapping("/course/list")
    @Operation(summary = "课程推荐", description = "按难度、分类筛选课程列表，返回符合条件的课程")
    public R<List<CourseDTO>> courseList(@RequestBody CourseSkillRequest request) {
        log.info("[Skill-Course] 收到课程筛选请求，level={}, categoryId={}, limit={}",
                request.getLevel(), request.getCategoryId(), request.getLimit());

        Integer levelInt = request.getLevel() != null ? LEVEL_MAP.get(request.getLevel()) : null;
        if (request.getLevel() != null && levelInt == null) {
            log.warn("[Skill-Course] 未知的难度等级: {}，将忽略 level 过滤", request.getLevel());
        }

        try {
            com.star.common.result.R<List<CourseDTO>> feignResult =
                    courseServiceClient.getCourseList(request.getCategoryId(), levelInt);

            List<CourseDTO> courses = (feignResult != null && feignResult.getData() != null)
                    ? feignResult.getData()
                    : List.of();

            // 按 limit 截断
            int limit = request.getLimit() != null ? request.getLimit() : 10;
            if (courses.size() > limit) {
                courses = courses.subList(0, limit);
            }

            log.info("[Skill-Course] 返回课程数: {}", courses.size());
            return R.ok(courses, "课程列表获取成功");

        } catch (Exception e) {
            log.error("[Skill-Course] 调用 course-service 失败: {}", e.getMessage());
            return R.error("课程服务暂时不可用：" + e.getMessage());
        }
    }

    // ── 技能注册表查询（可选，便于调试） ─────────────────────────────────

    /**
     * 查看当前 Nacos 技能注册表中已加载的技能列表（调试用）
     */
    @GetMapping("/registry")
    @Operation(summary = "技能注册表", description = "查看从 Nacos 加载的全部技能定义（调试用）")
    public R<List<SkillDefinition>> registry(SkillRegistryService skillRegistryService) {
        return R.ok(skillRegistryService.listAll(), "技能列表获取成功");
    }
}
