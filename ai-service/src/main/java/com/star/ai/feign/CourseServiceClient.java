package com.star.ai.feign;

import com.star.ai.dto.CourseDTO;
import com.star.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 课程服务Feign客户端
 * 
 * @author star
 */
@FeignClient(name = "course-service", path = "/course")
public interface CourseServiceClient {

    /**
     * 获取热门课程
     */
    @GetMapping("/hot")
    R<List<CourseDTO>> getHotCourses(@RequestParam(value = "limit", defaultValue = "10") Integer limit);

    /**
     * 根据分类获取课程（需要在course-service中实现，或使用现有接口）
     */
    @GetMapping("/list")
    R<List<CourseDTO>> getCourseList(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "level", required = false) Integer level
    );
}
