package com.star.ai.service;

import com.star.ai.dto.LearningReport;

/**
 * 学习分析服务接口
 * 
 * @author star
 */
public interface LearningAnalysisService {

    /**
     * 生成学习分析报告
     * 
     * @param userId 用户ID
     * @return 学习报告
     */
    LearningReport generateLearningReport(Long userId);

    /**
     * 获取学习建议
     * 
     * @param userId 用户ID
     * @return 建议列表
     */
    String getLearningAdvice(Long userId);
}
