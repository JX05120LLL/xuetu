package com.star.order.feign;

import com.star.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 课程服务Feign客户端
 * 
 * @author star
 */
@FeignClient(name = "course-service", path = "/course")
public interface CourseServiceClient {

    /**
     * 根据课程ID获取课程信息
     */
    @GetMapping("/{courseId}")
    R<Object> getCourseById(@PathVariable("courseId") Long courseId);

    /**
     * 增加学习人数
     */
    @PostMapping("/{courseId}/student-count")
    R<Boolean> increaseStudentCount(@PathVariable("courseId") Long courseId, 
                                    @RequestParam(value = "increment", defaultValue = "1") Integer increment);
}