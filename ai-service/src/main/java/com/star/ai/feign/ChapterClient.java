package com.star.ai.feign;

import com.star.ai.dto.ChapterDTO;
import com.star.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 课程章节 Feign 客户端
 */
@FeignClient(name = "course-service", contextId = "chapterClient", path = "/chapter")
public interface ChapterClient {

    @GetMapping("/course/{courseId}")
    R<List<ChapterDTO>> getChaptersByCourseId(@PathVariable("courseId") Long courseId);
}
