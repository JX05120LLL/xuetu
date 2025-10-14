package com.star.learning.feign;

import com.star.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 课程服务Feign客户端
 * 
 * @author star
 */
@FeignClient(name = "course-service", path = "/course")
public interface CourseServiceClient {

    /**
     * 获取课程详情
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    @GetMapping("/{courseId}")
    R<Object> getCourseById(@PathVariable("courseId") Long courseId);
}