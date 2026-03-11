package com.star.ai.feign;

import com.star.ai.dto.UserCourseSummaryDTO;
import com.star.common.dto.PageResult;
import com.star.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 订单/用户课程 Feign 客户端
 */
@FeignClient(name = "order-service", path = "/api/user-courses")
public interface OrderUserCourseClient {

    @GetMapping("/my")
    R<PageResult<UserCourseSummaryDTO>> getMyCourses(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "100") Integer size);
}
