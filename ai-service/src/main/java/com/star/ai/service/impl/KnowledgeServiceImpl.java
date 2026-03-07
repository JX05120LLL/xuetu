package com.star.ai.service.impl;

import com.star.ai.dto.CourseDTO;
import com.star.ai.dto.KnowledgeIngestResult;
import com.star.ai.entity.AiFaq;
import com.star.ai.feign.CourseServiceClient;
import com.star.ai.mapper.AiFaqMapper;
import com.star.ai.service.KnowledgeService;
import com.star.common.result.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 知识库管理服务实现
 *
 * 文档 ID 规则：Qdrant 要求 UUID 格式，使用逻辑 ID 的确定性 UUID 派生
 *   课程：UUID(nameUUIDFromBytes("course-{courseId}"))
 *   FAQ ：UUID(nameUUIDFromBytes("faq-{faqId}"))
 *
 * Metadata 字段：
 *   type       - 文档类型（course / faq）
 *   courseId   - 课程 ID（仅 course）
 *   faqId      - FAQ ID（仅 faq）
 *   category   - FAQ 分类（仅 faq）
 *   source     - 来源描述，用于在回答中标注引用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter;
    private final CourseServiceClient courseServiceClient;
    private final AiFaqMapper aiFaqMapper;

    // ─── 课程难度映射 ───────────────────────────────────────────────────────
    private static final Map<Integer, String> LEVEL_MAP = Map.of(
            1, "初级", 2, "中级", 3, "高级"
    );

    // ─── 全量入库 ──────────────────────────────────────────────────────────

    @Override
    public List<KnowledgeIngestResult> ingestAll() {
        List<KnowledgeIngestResult> results = new ArrayList<>();
        results.add(ingestAllCourses());
        results.add(ingestAllFaqs());
        return results;
    }

    // ─── 课程入库 ──────────────────────────────────────────────────────────

    @Override
    public KnowledgeIngestResult ingestAllCourses() {
        log.info("[知识库] 开始入库全量课程...");

        List<CourseDTO> courses = fetchAllCourses();
        if (courses.isEmpty()) {
            log.warn("[知识库] 未获取到任何课程，跳过入库");
            return KnowledgeIngestResult.builder()
                    .type("course").count(0).message("未获取到课程数据").build();
        }

        List<Document> documents = courses.stream()
                .map(this::buildCourseDocument)
                .collect(Collectors.toList());

        // 对过长描述的文档执行分块（大部分课程简介较短，不会触发）
        List<Document> chunked = tokenTextSplitter.apply(documents);

        vectorStore.add(chunked);

        log.info("[知识库] 课程入库完成，原始课程数: {}，入库文档块数: {}", courses.size(), chunked.size());
        return KnowledgeIngestResult.builder()
                .type("course")
                .count(chunked.size())
                .message("课程知识入库完成，共 " + courses.size() + " 门课程，" + chunked.size() + " 个文档块")
                .build();
    }

    @Override
    public KnowledgeIngestResult ingestSingleCourse(Long courseId) {
        log.info("[知识库] 增量更新课程, courseId={}", courseId);

        // 先删除旧的课程向量文档（按确定性 UUID 删除）
        String docId = toUuid("course-" + courseId);
        try {
            vectorStore.delete(List.of(docId));
            log.info("[知识库] 已删除旧课程向量文档: {}", docId);
        } catch (Exception e) {
            log.warn("[知识库] 删除旧课程向量文档失败（可能不存在），继续入库: {}", e.getMessage());
        }

        // 重新获取课程信息
        List<CourseDTO> courses = fetchAllCourses();
        CourseDTO course = courses.stream()
                .filter(c -> courseId.equals(c.getId()))
                .findFirst()
                .orElse(null);

        if (course == null) {
            return KnowledgeIngestResult.builder()
                    .type("course").count(0).message("课程 " + courseId + " 不存在").build();
        }

        Document doc = buildCourseDocument(course);
        List<Document> chunked = tokenTextSplitter.apply(List.of(doc));
        vectorStore.add(chunked);

        log.info("[知识库] 单课程入库完成, courseId={}, chunks={}", courseId, chunked.size());
        return KnowledgeIngestResult.builder()
                .type("course")
                .count(chunked.size())
                .message("课程 " + courseId + " 入库完成")
                .build();
    }

    // ─── FAQ 入库 ──────────────────────────────────────────────────────────

    @Override
    public KnowledgeIngestResult ingestAllFaqs() {
        log.info("[知识库] 开始入库全量 FAQ...");

        List<AiFaq> faqs = aiFaqMapper.selectAllEnabled();
        if (faqs.isEmpty()) {
            log.warn("[知识库] 未查到启用的 FAQ");
            return KnowledgeIngestResult.builder()
                    .type("faq").count(0).message("未查到 FAQ 数据").build();
        }

        List<Document> documents = faqs.stream()
                .map(this::buildFaqDocument)
                .collect(Collectors.toList());

        vectorStore.add(documents);

        log.info("[知识库] FAQ 入库完成，共 {} 条", documents.size());
        return KnowledgeIngestResult.builder()
                .type("faq")
                .count(documents.size())
                .message("FAQ 知识入库完成，共 " + documents.size() + " 条")
                .build();
    }

    // ─── 按类型清空 ────────────────────────────────────────────────────────

    @Override
    public void deleteByType(String type) {
        log.info("[知识库] 清空类型为 '{}' 的向量文档...", type);

        if ("course".equals(type)) {
            List<CourseDTO> courses = fetchAllCourses();
            List<String> ids = courses.stream()
                    .map(c -> toUuid("course-" + c.getId()))
                    .collect(Collectors.toList());
            if (!ids.isEmpty()) {
                vectorStore.delete(ids);
                log.info("[知识库] 已删除 {} 条课程向量文档", ids.size());
            }
        } else if ("faq".equals(type)) {
            List<AiFaq> faqs = aiFaqMapper.selectAllEnabled();
            List<String> ids = faqs.stream()
                    .map(f -> toUuid("faq-" + f.getId()))
                    .collect(Collectors.toList());
            if (!ids.isEmpty()) {
                vectorStore.delete(ids);
                log.info("[知识库] 已删除 {} 条 FAQ 向量文档", ids.size());
            }
        } else {
            log.warn("[知识库] 未知类型: {}，不执行删除", type);
        }
    }

    // ─── 私有辅助方法 ──────────────────────────────────────────────────────

    /**
     * 构建课程 Document
     * 使用确定性 ID "course-{courseId}"，重复入库时 Qdrant 会覆盖旧向量。
     */
    private Document buildCourseDocument(CourseDTO course) {
        String levelName = LEVEL_MAP.getOrDefault(course.getLevel(), "未知");
        String description = course.getDescription() == null ? "" : course.getDescription();

        // 文档内容：让 LLM 看到的参考文本
        String content = String.format(
                "课程名称：%s\n难度级别：%s\n课程简介：%s",
                course.getTitle(), levelName, description
        );

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "course");
        metadata.put("courseId", String.valueOf(course.getId()));
        metadata.put("source", "《" + course.getTitle() + "》课程简介");

        // Qdrant 要求 UUID，用逻辑 ID 派生确定性 UUID
        return new Document(toUuid("course-" + course.getId()), content, metadata);
    }

    /**
     * 构建 FAQ Document
     * Q&A 作为整体存入，避免答案和问题被分离到不同向量。
     */
    private Document buildFaqDocument(AiFaq faq) {
        String content = String.format(
                "问题：%s\n回答：%s",
                faq.getQuestion(), faq.getAnswer()
        );

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "faq");
        metadata.put("faqId", String.valueOf(faq.getId()));
        metadata.put("category", faq.getCategory());
        metadata.put("source", "FAQ：" + faq.getQuestion());

        return new Document(toUuid("faq-" + faq.getId()), content, metadata);
    }

    /** Qdrant 要求文档 ID 为 UUID 格式，由逻辑 ID 派生确定性 UUID */
    private static String toUuid(String logicalId) {
        return UUID.nameUUIDFromBytes(logicalId.getBytes(StandardCharsets.UTF_8)).toString();
    }

    /**
     * 通过 Feign 拉取课程列表。
     *
     * 使用 /course/hot?limit=500 而非 /course/list，原因：
     *   /course/list 返回 IPage（分页），/course/hot 返回 List，类型与 Feign 声明一致。
     *   limit=500 足以覆盖平台现有全量课程。
     */
    private List<CourseDTO> fetchAllCourses() {
        try {
            R<List<CourseDTO>> result = courseServiceClient.getHotCourses(500);
            if (result != null && result.getData() != null) {
                return result.getData();
            }
        } catch (Exception e) {
            log.error("[知识库] 从 course-service 获取课程列表失败: {}", e.getMessage());
        }
        return List.of();
    }
}
