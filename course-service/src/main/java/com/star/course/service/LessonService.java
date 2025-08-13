package com.star.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.course.dto.LessonDTO;
import com.star.course.dto.LessonRequest;
import com.star.course.entity.Lesson;

import java.util.List;

/**
 * 课时服务接口
 * 
 * @author star
 */
public interface LessonService extends IService<Lesson> {

    /**
     * 根据章节ID查询课时列表
     * 
     * @param chapterId 章节ID
     * @return 课时列表
     */
    List<LessonDTO> getLessonsByChapterId(Long chapterId);

    /**
     * 根据ID查询课时详情
     * 
     * @param id 课时ID
     * @return 课时详情
     */
    LessonDTO getLessonDetail(Long id);

    /**
     * 创建课时
     * 
     * @param chapterId 章节ID
     * @param request 创建请求
     * @return 课时ID
     */
    Long createLesson(Long chapterId, LessonRequest request);

    /**
     * 更新课时
     * 
     * @param id 课时ID
     * @param request 更新请求
     * @return 是否成功
     */
    Boolean updateLesson(Long id, LessonRequest request);

    /**
     * 删除课时
     * 
     * @param id 课时ID
     * @return 是否成功
     */
    Boolean deleteLesson(Long id);

    /**
     * 批量更新课时排序
     * 
     * @param chapterId 章节ID
     * @param lessonIds 课时ID列表（按新的排序）
     * @return 是否成功
     */
    Boolean updateLessonSort(Long chapterId, List<Long> lessonIds);

    /**
     * 计算课程总时长
     * 
     * @param courseId 课程ID
     * @return 总时长（分钟）
     */
    Integer calculateCourseDuration(Long courseId);
}