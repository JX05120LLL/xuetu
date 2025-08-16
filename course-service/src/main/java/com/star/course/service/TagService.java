package com.star.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.course.dto.CourseTagRequest;
import com.star.course.dto.TagDTO;
import com.star.course.dto.TagRequest;
import com.star.course.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 * 
 * @author star
 */
public interface TagService extends IService<Tag> {

    /**
     * 查询所有标签列表
     * 
     * @return 标签列表
     */
    List<TagDTO> getAllTags();

    /**
     * 查询热门标签
     * 
     * @param limit 数量限制
     * @return 热门标签列表
     */
    List<TagDTO> getPopularTags(Integer limit);

    /**
     * 根据课程ID查询标签列表
     * 
     * @param courseId 课程ID
     * @return 标签列表
     */
    List<TagDTO> getTagsByCourseId(Long courseId);

    /**
     * 根据ID查询标签详情
     * 
     * @param id 标签ID
     * @return 标签详情
     */
    TagDTO getTagDetail(Long id);

    /**
     * 创建标签
     * 
     * @param request 创建请求
     * @return 标签ID
     */
    Long createTag(TagRequest request);

    /**
     * 更新标签
     * 
     * @param id 标签ID
     * @param request 更新请求
     * @return 是否成功
     */
    Boolean updateTag(Long id, TagRequest request);

    /**
     * 删除标签
     * 
     * @param id 标签ID
     * @return 是否成功
     */
    Boolean deleteTag(Long id);

    /**
     * 检查标签名称是否存在
     * 
     * @param name 标签名称
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否存在
     */
    Boolean existsByName(String name, Long excludeId);

    /**
     * 为课程设置标签
     * 
     * @param request 课程标签设置请求
     * @return 是否成功
     */
    Boolean setCourseTag(CourseTagRequest request);

    /**
     * 为课程添加单个标签
     * 
     * @param courseId 课程ID
     * @param tagId 标签ID
     * @return 是否成功
     */
    Boolean addCourseTag(Long courseId, Long tagId);

    /**
     * 移除课程标签
     * 
     * @param courseId 课程ID
     * @param tagId 标签ID
     * @return 是否成功
     */
    Boolean removeCourseTag(Long courseId, Long tagId);

    /**
     * 清除课程的所有标签
     * 
     * @param courseId 课程ID
     * @return 是否成功
     */
    Boolean clearCourseTags(Long courseId);

    /**
     * 统计标签使用次数
     * 
     * @param tagId 标签ID
     * @return 使用次数
     */
    Integer countUsage(Long tagId);
}