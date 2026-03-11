package com.star.ai.feign;

import com.star.ai.dto.LearningStatsDTO;
import com.star.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 学习服务Feign客户端
 * 
 * @author star
 */
@FeignClient(name = "learning-service", path = "/learning")
public interface LearningServiceClient {

    /**
     * 获取用户学习统计
     */
    @GetMapping("/record/stats")
    R<LearningStatsDTO> getLearningStats(@RequestHeader("X-User-Id") Long userId);

    /**
     * 获取指定课程的学习进度百分比
     */
    @GetMapping("/record/progress/{courseId}")
    R<Double> getCourseProgress(@RequestHeader("X-User-Id") Long userId, @PathVariable("courseId") Long courseId);

    /**
     * 获取指定课程的学习记录列表
     */
    @GetMapping("/record/course/{courseId}")
    R<List<Object>> getCourseRecords(@RequestHeader("X-User-Id") Long userId, @PathVariable("courseId") Long courseId);
}
