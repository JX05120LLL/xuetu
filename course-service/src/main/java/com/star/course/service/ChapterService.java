package com.star.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.course.dto.ChapterDTO;
import com.star.course.dto.ChapterRequest;
import com.star.course.entity.Chapter;

import java.util.List;

/**
 * 章节服务接口
 * 
 * @author star
 */
public interface ChapterService extends IService<Chapter> {

    /**
     * 根据课程ID查询章节列表
     * 
     * @param courseId 课程ID
     * @return 章节列表
     */
    List<ChapterDTO> getChaptersByCourseId(Long courseId);

    /**
     * 根据ID查询章节详情
     * 
     * @param id 章节ID
     * @return 章节详情
     */
    ChapterDTO getChapterDetail(Long id);

    /**
     * 创建章节
     * 
     * @param courseId 课程ID
     * @param request 创建请求
     * @return 章节ID
     */
    Long createChapter(Long courseId, ChapterRequest request);

    /**
     * 更新章节
     * 
     * @param id 章节ID
     * @param request 更新请求
     * @return 是否成功
     */
    Boolean updateChapter(Long id, ChapterRequest request);

    /**
     * 删除章节
     * 
     * @param id 章节ID
     * @return 是否成功
     */
    Boolean deleteChapter(Long id);

    /**
     * 调整章节排序
     * 
     * @param courseId 课程ID
     * @param chapterIds 章节ID列表（按新的排序）
     * @return 是否成功
     */
    Boolean sortChapters(Long courseId, List<Long> chapterIds);

    /**
     * 查询课程的章节数量
     * 
     * @param courseId 课程ID
     * @return 章节数量
     */
    Integer getChapterCountByCourseId(Long courseId);
}