package com.star.ai.service;

import com.star.ai.dto.KnowledgeIngestResult;

import java.util.List;

/**
 * 知识库管理服务
 * 负责将课程、FAQ 等业务数据向量化后写入 Qdrant，供 RAG 检索使用。
 */
public interface KnowledgeService {

    /**
     * 全量入库：课程 + FAQ
     */
    List<KnowledgeIngestResult> ingestAll();

    /**
     * 入库所有课程
     */
    KnowledgeIngestResult ingestAllCourses();

    /**
     * 入库所有 FAQ
     */
    KnowledgeIngestResult ingestAllFaqs();

    /**
     * 增量更新单门课程（先删旧向量再重新入库）
     *
     * @param courseId 课程 ID
     */
    KnowledgeIngestResult ingestSingleCourse(Long courseId);

    /**
     * 清空指定类型的向量文档
     *
     * @param type 文档类型，如 "course" / "faq"
     */
    void deleteByType(String type);
}
