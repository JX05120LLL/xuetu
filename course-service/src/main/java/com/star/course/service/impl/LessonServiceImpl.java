package com.star.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.course.dto.LessonDTO;
import com.star.course.dto.LessonRequest;
import com.star.course.entity.Chapter;
import com.star.course.entity.Lesson;
import com.star.course.exception.CourseNotFoundException;
import com.star.course.exception.CourseServiceException;
import com.star.course.exception.LessonException;
import com.star.course.mapper.LessonMapper;
import com.star.course.service.ChapterService;
import com.star.course.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课时服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LessonServiceImpl extends ServiceImpl<LessonMapper, Lesson> implements LessonService {

    private final LessonMapper lessonMapper;
    private final ChapterService chapterService;

    @Override
    public List<LessonDTO> getLessonsByChapterId(Long chapterId) {
        log.info("查询章节课时列表: chapterId={}", chapterId);

        LambdaQueryWrapper<Lesson> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Lesson::getChapterId, chapterId)
                   .orderByAsc(Lesson::getSortOrder);

        List<Lesson> lessons = list(queryWrapper);
        return lessons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LessonDTO getLessonDetail(Long id) {
        log.info("查询课时详情: id={}", id);

        Lesson lesson = getById(id);
        if (lesson == null) {
            throw LessonException.notFound(id);
        }

        return convertToDTO(lesson);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createLesson(Long chapterId, LessonRequest request) {
        log.info("创建课时: chapterId={}, title={}", chapterId, request.getTitle());

        // 验证章节是否存在
        Chapter chapter = chapterService.getById(chapterId);
        if (chapter == null) {
            throw CourseServiceException.paramError("章节不存在: " + chapterId);
        }

        // 验证课时时长
        if (request.getDuration() != null && request.getDuration() < 0) {
            throw LessonException.invalidDuration(request.getDuration());
        }

        // 创建课时实体
        Lesson lesson = new Lesson();
        BeanUtils.copyProperties(request, lesson);
        lesson.setChapterId(chapterId);
        
        // 设置排序值
        if (lesson.getSortOrder() == null) {
            Integer maxSort = lessonMapper.getMaxSortOrderByChapterId(chapterId);
            lesson.setSortOrder(maxSort + 1);
        }
        
        lesson.setCreatedTime(LocalDateTime.now());
        lesson.setUpdatedTime(LocalDateTime.now());

        // 保存课时
        if (!save(lesson)) {
            throw CourseServiceException.internalError("创建课时失败");
        }

        log.info("课时创建成功: id={}", lesson.getId());
        return lesson.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLesson(Long id, LessonRequest request) {
        log.info("更新课时: id={}", id);

        // 检查课时是否存在
        Lesson existsLesson = getById(id);
        if (existsLesson == null) {
            throw LessonException.notFound(id);
        }

        // 验证课时时长
        if (request.getDuration() != null && request.getDuration() < 0) {
            throw LessonException.invalidDuration(request.getDuration());
        }

        // 更新课时信息
        Lesson lesson = new Lesson();
        BeanUtils.copyProperties(request, lesson);
        lesson.setId(id);
        lesson.setUpdatedTime(LocalDateTime.now());

        boolean success = updateById(lesson);
        if (!success) {
            throw CourseServiceException.internalError("更新课时失败");
        }

        log.info("课时更新成功: id={}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteLesson(Long id) {
        log.info("删除课时: id={}", id);

        // 检查课时是否存在
        Lesson lesson = getById(id);
        if (lesson == null) {
            throw LessonException.notFound(id);
        }

        boolean success = removeById(id);
        if (!success) {
            throw CourseServiceException.internalError("删除课时失败");
        }

        log.info("课时删除成功: id={}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLessonSort(Long chapterId, List<Long> lessonIds) {
        log.info("批量更新课时排序: chapterId={}, lessonIds={}", chapterId, lessonIds);

        for (int i = 0; i < lessonIds.size(); i++) {
            Long lessonId = lessonIds.get(i);
            Lesson lesson = new Lesson();
            lesson.setId(lessonId);
            lesson.setSortOrder(i + 1);
            lesson.setUpdatedTime(LocalDateTime.now());
            updateById(lesson);
        }

        log.info("课时排序更新成功");
        return true;
    }

    @Override
    public Integer calculateCourseDuration(Long courseId) {
        log.info("计算课程总时长: courseId={}", courseId);
        return lessonMapper.sumDurationByCourseId(courseId);
    }

    /**
     * 转换为DTO
     */
    private LessonDTO convertToDTO(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        BeanUtils.copyProperties(lesson, dto);
        
        // 设置是否免费的文字描述
        dto.setIsFreeLabel(lesson.getIsFree() != null && lesson.getIsFree() == 1 ? "免费" : "付费");
        
        return dto;
    }
}