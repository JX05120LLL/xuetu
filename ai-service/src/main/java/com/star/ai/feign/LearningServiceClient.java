package com.star.ai.feign;

import com.star.ai.dto.LearningStatsDTO;
import com.star.ai.dto.UserCourseProgressDTO;
import com.star.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

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
     * 获取用户课程进度列表（需要在learning-service中实现）
     */
    @GetMapping("/record/progress/list")
    R<List<UserCourseProgressDTO>> getUserCourseProgress(@RequestHeader("X-User-Id") Long userId);
}
