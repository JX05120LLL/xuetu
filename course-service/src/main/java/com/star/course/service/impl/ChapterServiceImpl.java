package com.star.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.course.dto.ChapterDTO;
import com.star.course.dto.ChapterRequest;
import com.star.course.dto.LessonDTO;
import com.star.course.entity.Chapter;
import com.star.course.entity.Lesson;
import com.star.course.exception.ChapterException;
import com.star.course.exception.CourseServiceException;
import com.star.course.mapper.ChapterMapper;
import com.star.course.service.ChapterService;
import com.star.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 章节服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    private final ChapterMapper chapterMapper;
    private final CourseService courseService;
    private final com.star.course.mapper.LessonMapper lessonMapper;

    @Override
    public List<ChapterDTO> getChaptersByCourseId(Long courseId) {
        log.info("查询课程章节列表: courseId={}", courseId);

        // 使用MyBatis Plus的LambdaQueryWrapper
        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getCourseId, courseId)
                    .orderByAsc(Chapter::getSortOrder)
                    .orderByAsc(Chapter::getId);

        List<Chapter> chapters = list(queryWrapper);
        return chapters.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChapterDTO getChapterDetail(Long id) {
        log.info("查询章节详情: id={}", id);

        Chapter chapter = getById(id);
        if (chapter == null) {
            throw ChapterException.notFound(id);
        }

        return convertToDTO(chapter);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createChapter(Long courseId, ChapterRequest request) {
        log.info("创建章节: courseId={}, title={}", courseId, request.getTitle());

        // 验证课程是否存在
        if (courseService.getById(courseId) == null) {
            throw CourseServiceException.paramError("课程不存在: " + courseId);
        }

        // 设置排序值
        if (request.getSortOrder() == null) {
            Integer maxSort = chapterMapper.getMaxSortOrderByCourseId(courseId);
            request.setSortOrder(maxSort + 1);
        }

        // 创建章节
        Chapter chapter = new Chapter();
        BeanUtils.copyProperties(request, chapter);
        chapter.setCourseId(courseId);
        chapter.setCreatedTime(LocalDateTime.now());
        chapter.setUpdatedTime(LocalDateTime.now());

        if (!save(chapter)) {
            throw CourseServiceException.internalError("创建章节失败");
        }

        log.info("章节创建成功: id={}", chapter.getId());
        return chapter.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateChapter(Long id, ChapterRequest request) {
        log.info("更新章节: id={}", id);

        // 检查章节是否存在
        Chapter existsChapter = getById(id);
        if (existsChapter == null) {
            throw ChapterException.notFound(id);
        }

        // 更新章节
        Chapter chapter = new Chapter();
        BeanUtils.copyProperties(request, chapter);
        chapter.setId(id);
        chapter.setUpdatedTime(LocalDateTime.now());

        boolean success = updateById(chapter);
        if (!success) {
            throw CourseServiceException.internalError("更新章节失败");
        }

        log.info("章节更新成功: id={}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteChapter(Long id) {
        log.info("删除章节: id={}", id);

        // 检查章节是否存在
        Chapter chapter = getById(id);
        if (chapter == null) {
            throw ChapterException.notFound(id);
        }

        // 检查章节下是否有课时
        LambdaQueryWrapper<Lesson> lessonQueryWrapper = new LambdaQueryWrapper<>();
        lessonQueryWrapper.eq(Lesson::getChapterId, id);
        // 这里需要注入LessonService，暂时简化处理

        boolean success = removeById(id);
        if (!success) {
            throw CourseServiceException.internalError("删除章节失败");
        }

        log.info("章节删除成功: id={}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean sortChapters(Long courseId, List<Long> chapterIds) {
        log.info("调整章节排序: courseId={}, chapterIds={}", courseId, chapterIds);

        // 批量更新排序
        for (int i = 0; i < chapterIds.size(); i++) {
            Long chapterId = chapterIds.get(i);
            Chapter chapter = new Chapter();
            chapter.setId(chapterId);
            chapter.setSortOrder(i + 1);
            chapter.setUpdatedTime(LocalDateTime.now());
            updateById(chapter);
        }

        log.info("章节排序调整成功: courseId={}", courseId);
        return true;
    }

    @Override
    public Integer getChapterCountByCourseId(Long courseId) {
        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getCourseId, courseId);
        return Math.toIntExact(count(queryWrapper));
    }

    /**
     * 转换为DTO
     */
    private ChapterDTO convertToDTO(Chapter chapter) {
        ChapterDTO dto = new ChapterDTO();
        BeanUtils.copyProperties(chapter, dto);

        // 查询该章节的所有课时
        LambdaQueryWrapper<Lesson> lessonWrapper = new LambdaQueryWrapper<>();
        lessonWrapper.eq(Lesson::getChapterId, chapter.getId())
                     .orderByAsc(Lesson::getSortOrder)
                     .orderByAsc(Lesson::getId);
        
        List<Lesson> lessons = lessonMapper.selectList(lessonWrapper);
        
        // 转换为DTO
        List<LessonDTO> lessonDTOs = lessons.stream()
                .map(this::convertLessonToDTO)
                .collect(Collectors.toList());
        
        dto.setLessons(lessonDTOs);
        dto.setLessonCount(lessonDTOs.size());

        return dto;
    }
    
    /**
     * 转换课时为DTO
     */
    private LessonDTO convertLessonToDTO(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        BeanUtils.copyProperties(lesson, dto);
        return dto;
    }
}