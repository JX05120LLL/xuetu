package com.star.order.feign;

import com.star.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 课程服务Feign客户端
 * 
 * @author star
 */
@FeignClient(name = "course-service", path = "/api")
public interface CourseServiceClient {

    /**
     * 根据课程ID获取课程信息
     */
    @GetMapping("/courses/{courseId}")
    R<Object> getCourseById(@PathVariable("courseId") Long courseId);

    /**
     * 批量获取课程信息
     */
    @PostMapping("/courses/batch")
    R<List<Object>> getCoursesByIds(@RequestBody List<Long> courseIds);

    /**
     * 检查课程是否可购买
     */
    @GetMapping("/courses/{courseId}/purchasable")
    R<Boolean> checkCoursePurchasable(@PathVariable("courseId") Long courseId);

    /**
     * 更新课程销量
     */
    @PostMapping("/courses/{courseId}/sales")
    R<Void> updateCourseSales(@PathVariable("courseId") Long courseId);
}