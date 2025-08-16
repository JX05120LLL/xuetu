package com.star.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.course.dto.CourseTagRequest;
import com.star.course.dto.TagDTO;
import com.star.course.dto.TagRequest;
import com.star.course.entity.CourseTag;
import com.star.course.entity.Tag;
import com.star.course.exception.TagException;
import com.star.course.mapper.CourseTagMapper;
import com.star.course.mapper.TagMapper;
import com.star.course.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private final TagMapper tagMapper;
    private final CourseTagMapper courseTagMapper;

    @Override
    public List<TagDTO> getAllTags() {
        List<Tag> tags = list();
        return tags.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TagDTO> getPopularTags(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认查询10个热门标签
        }
        
        List<Tag> tags = tagMapper.selectPopularTags(limit);
        return tags.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TagDTO> getTagsByCourseId(Long courseId) {
        List<Tag> tags = tagMapper.selectByCourseId(courseId);
        return tags.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public TagDTO getTagDetail(Long id) {
        Tag tag = getById(id);
        if (tag == null) {
            throw TagException.notFound(id);
        }
        return convertToDTO(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTag(TagRequest request) {
        // 验证参数
        validateTagRequest(request);
        
        // 检查名称是否存在
        if (existsByName(request.getName(), null)) {
            throw TagException.nameExists(request.getName());
        }

        // 创建标签
        Tag tag = new Tag();
        BeanUtils.copyProperties(request, tag);
        
        // 设置默认颜色
        if (!StringUtils.hasText(tag.getColor())) {
            tag.setColor(generateDefaultColor());
        }

        save(tag);
        log.info("创建标签成功，ID: {}, 名称: {}", tag.getId(), tag.getName());
        return tag.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTag(Long id, TagRequest request) {
        // 验证参数
        validateTagRequest(request);
        
        // 检查标签是否存在
        Tag existingTag = getById(id);
        if (existingTag == null) {
            throw TagException.notFound(id);
        }

        // 检查名称是否存在（排除自己）
        if (existsByName(request.getName(), id)) {
            throw TagException.nameExists(request.getName());
        }

        // 更新标签
        Tag tag = new Tag();
        tag.setId(id);
        BeanUtils.copyProperties(request, tag);
        
        // 如果颜色为空，保持原有颜色
        if (!StringUtils.hasText(tag.getColor())) {
            tag.setColor(existingTag.getColor());
        }

        boolean success = updateById(tag);
        if (success) {
            log.info("更新标签成功，ID: {}, 名称: {}", id, tag.getName());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTag(Long id) {
        // 检查标签是否存在
        Tag tag = getById(id);
        if (tag == null) {
            throw TagException.notFound(id);
        }

        // 检查是否被课程使用
        Integer usageCount = tagMapper.countUsageByTagId(id);
        if (usageCount > 0) {
            throw TagException.usedByCoursesCannotDelete(id);
        }

        // 删除标签
        boolean success = removeById(id);
        if (success) {
            log.info("删除标签成功，ID: {}, 名称: {}", id, tag.getName());
        }
        return success;
    }

    @Override
    public Boolean existsByName(String name, Long excludeId) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, name);
        if (excludeId != null) {
            wrapper.ne(Tag::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setCourseTag(CourseTagRequest request) {
        Long courseId = request.getCourseId();
        List<Long> tagIds = request.getTagIds();

        // 验证标签数量
        if (tagIds.size() > 10) {
            throw TagException.tooManyTagsForCourse();
        }

        // 验证所有标签是否存在
        for (Long tagId : tagIds) {
            if (getById(tagId) == null) {
                throw TagException.notFound(tagId);
            }
        }

        // 先删除课程现有的所有标签关联
        courseTagMapper.deleteByCourseId(courseId);

        // 批量插入新的标签关联
        List<CourseTag> courseTags = tagIds.stream()
                .map(tagId -> {
                    CourseTag courseTag = new CourseTag();
                    courseTag.setCourseId(courseId);
                    courseTag.setTagId(tagId);
                    return courseTag;
                })
                .collect(Collectors.toList());

        // 批量保存
        for (CourseTag courseTag : courseTags) {
            courseTagMapper.insert(courseTag);
        }

        log.info("设置课程标签成功，课程ID: {}, 标签数量: {}", courseId, tagIds.size());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addCourseTag(Long courseId, Long tagId) {
        // 验证标签是否存在
        if (getById(tagId) == null) {
            throw TagException.notFound(tagId);
        }

        // 检查课程标签数量是否超限
        Integer tagCount = tagMapper.countTagsByCourseId(courseId);
        if (tagCount >= 10) {
            throw TagException.tooManyTagsForCourse();
        }

        // 检查关联是否已存在
        LambdaQueryWrapper<CourseTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTag::getCourseId, courseId)
               .eq(CourseTag::getTagId, tagId);
        
        if (courseTagMapper.selectCount(wrapper) > 0) {
            return true; // 已存在，视为成功
        }

        // 创建关联
        CourseTag courseTag = new CourseTag();
        courseTag.setCourseId(courseId);
        courseTag.setTagId(tagId);
        
        int result = courseTagMapper.insert(courseTag);
        log.info("添加课程标签成功，课程ID: {}, 标签ID: {}", courseId, tagId);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeCourseTag(Long courseId, Long tagId) {
        int result = courseTagMapper.deleteByCourseIdAndTagId(courseId, tagId);
        log.info("移除课程标签，课程ID: {}, 标签ID: {}, 影响行数: {}", courseId, tagId, result);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean clearCourseTags(Long courseId) {
        int result = courseTagMapper.deleteByCourseId(courseId);
        log.info("清除课程所有标签，课程ID: {}, 影响行数: {}", courseId, result);
        return result >= 0;
    }

    @Override
    public Integer countUsage(Long tagId) {
        return tagMapper.countUsageCount(tagId);
    }

    /**
     * 转换为DTO
     */
    private TagDTO convertToDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        BeanUtils.copyProperties(tag, dto);
        
        // 设置使用次数
        dto.setUsageCount(countUsage(tag.getId()));
        
        return dto;
    }

    /**
     * 验证标签请求参数
     */
    private void validateTagRequest(TagRequest request) {
        if (!StringUtils.hasText(request.getName())) {
            throw TagException.nameCannotBeEmpty();
        }
        
        if (request.getName().length() > 20) {
            throw TagException.nameTooLong();
        }
        
        if (StringUtils.hasText(request.getColor()) && 
            !request.getColor().matches("^#[0-9A-Fa-f]{6}$")) {
            throw TagException.invalidColorFormat();
        }
    }

    /**
     * 生成默认颜色
     */
    private String generateDefaultColor() {
        String[] colors = {
            "#FF5722", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
            "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
            "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800"
        };
        return colors[(int) (Math.random() * colors.length)];
    }
}