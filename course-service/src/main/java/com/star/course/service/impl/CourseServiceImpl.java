package com.star.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.common.dto.PageParam;
import com.star.common.utils.PageUtil;
import com.star.course.dto.*;
import com.star.course.entity.Course;
import com.star.course.exception.CourseNotFoundException;
import com.star.course.exception.CourseServiceException;
import com.star.course.mapper.CourseMapper;
import com.star.course.service.CategoryService;
import com.star.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final CourseMapper courseMapper;
    private final CategoryService categoryService;

    @Override
    public IPage<CourseDTO> getCoursePage(PageParam pageParam, String title, Long categoryId, Integer status, Integer level) {
        log.info("分页查询课程列表: current={}, size={}, title={}, categoryId={}, status={}, level={}",
                pageParam.getCurrent(), pageParam.getSize(), title, categoryId, status, level);

        Page<Course> page = PageUtil.buildPage(pageParam);
        
        // 使用MyBatis Plus的QueryWrapper构建查询条件
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(title), Course::getTitle, title)
                    .eq(categoryId != null, Course::getCategoryId, categoryId)
                    .eq(status != null, Course::getStatus, status)
                    .eq(level != null, Course::getLevel, level)
                    .orderByDesc(Course::getCreatedTime);

        IPage<Course> coursePage = page(page, queryWrapper);

        // 转换为DTO
        return coursePage.convert(this::convertToDTO);
    }

    @Override
    public CourseDTO getCourseDetail(Long id) {
        log.info("查询课程详情: id={}", id);

        Course course = getById(id);
        if (course == null) {
            throw CourseNotFoundException.notFound(id);
        }

        return convertToDTO(course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCourse(CourseCreateRequest request) {
        log.info("创建课程: title={}", request.getTitle());

        // 验证分类是否存在
        CategoryDTO category = categoryService.getCategoryDetail(request.getCategoryId());
        if (category == null) {
            throw CourseServiceException.paramError("分类不存在: " + request.getCategoryId());
        }

        // 创建课程实体
        Course course = new Course();
        BeanUtils.copyProperties(request, course);
        course.setStudentCount(0);
        course.setTotalDuration(0);
        course.setCreatedTime(LocalDateTime.now());
        course.setUpdatedTime(LocalDateTime.now());

        // 保存课程
        if (!save(course)) {
            throw CourseServiceException.internalError("创建课程失败");
        }

        log.info("课程创建成功: id={}", course.getId());
        return course.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCourse(Long id, CourseUpdateRequest request) {
        log.info("更新课程: id={}", id);

        // 检查课程是否存在
        Course existsCourse = getById(id);
        if (existsCourse == null) {
            throw CourseNotFoundException.notFound(id);
        }

        // 验证分类是否存在（如果提供了分类ID）
        if (request.getCategoryId() != null) {
            CategoryDTO category = categoryService.getCategoryDetail(request.getCategoryId());
            if (category == null) {
                throw CourseServiceException.paramError("分类不存在: " + request.getCategoryId());
            }
        }

        // 更新课程信息
        Course course = new Course();
        BeanUtils.copyProperties(request, course);
        course.setId(id);
        course.setUpdatedTime(LocalDateTime.now());

        boolean success = updateById(course);
        if (!success) {
            throw CourseServiceException.internalError("更新课程失败");
        }

        log.info("课程更新成功: id={}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCourse(Long id) {
        log.info("删除课程: id={}", id);

        // 检查课程是否存在
        Course course = getById(id);
        if (course == null) {
            throw CourseNotFoundException.notFound(id);
        }

        // 检查课程状态，已发布的课程不能删除
        if (Course.Status.PUBLISHED.getValue().equals(course.getStatus())) {
            throw CourseServiceException.paramError("已发布的课程不能删除");
        }

        boolean success = removeById(id);
        if (!success) {
            throw CourseServiceException.internalError("删除课程失败");
        }

        log.info("课程删除成功: id={}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean publishCourse(Long id) {
        log.info("发布课程: id={}", id);

        Course course = getById(id);
        if (course == null) {
            throw CourseNotFoundException.notFound(id);
        }

        if (Course.Status.PUBLISHED.getValue().equals(course.getStatus())) {
            throw CourseServiceException.paramError("课程已经发布");
        }

        course.setStatus(Course.Status.PUBLISHED.getValue());
        course.setUpdatedTime(LocalDateTime.now());

        boolean success = updateById(course);
        if (!success) {
            throw CourseServiceException.internalError("发布课程失败");
        }

        log.info("课程发布成功: id={}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean offlineCourse(Long id) {
        log.info("下架课程: id={}", id);

        Course course = getById(id);
        if (course == null) {
            throw CourseNotFoundException.notFound(id);
        }

        course.setStatus(Course.Status.OFFLINE.getValue());
        course.setUpdatedTime(LocalDateTime.now());

        boolean success = updateById(course);
        if (!success) {
            throw CourseServiceException.internalError("下架课程失败");
        }

        log.info("课程下架成功: id={}", id);
        return true;
    }

    @Override
    public List<CourseDTO> getHotCourses(Integer limit) {
        log.info("查询热门课程: limit={}", limit);

        List<Course> courses = courseMapper.selectHotCourses(limit);
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<CourseDTO> searchCourses(PageParam pageParam, String keyword) {
        log.info("搜索课程: keyword={}", keyword);

        Page<Course> page = PageUtil.buildPage(pageParam);
        
        // 使用MyBatis Plus的QueryWrapper进行模糊搜索
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(StringUtils.hasText(keyword), wrapper -> 
            wrapper.like(Course::getTitle, keyword)
                   .or()
                   .like(Course::getDescription, keyword)
        ).eq(Course::getStatus, Course.Status.PUBLISHED.getValue())
         .orderByDesc(Course::getStudentCount);

        IPage<Course> coursePage = page(page, queryWrapper);
        return coursePage.convert(this::convertToDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean increaseStudentCount(Long id, Integer increment) {
        log.info("增加课程学习人数: id={}, increment={}", id, increment);

        Integer rows = courseMapper.updateStudentCount(id, increment);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchUpdateStatus(List<Long> ids, Integer status) {
        log.info("批量更新课程状态: ids={}, status={}", ids, status);

        // 使用MyBatis Plus的批量更新
        LambdaUpdateWrapper<Course> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Course::getStatus, status)
                    .set(Course::getUpdatedTime, LocalDateTime.now())
                    .in(Course::getId, ids);

        boolean success = update(updateWrapper);
        return success;
    }

    @Override
    public Integer getCourseCountByCategoryId(Long categoryId) {
        return courseMapper.countCoursesByCategoryId(categoryId);
    }

    /**
     * 转换为DTO
     */
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        BeanUtils.copyProperties(course, dto);

        // 设置级别名称
        for (Course.Level level : Course.Level.values()) {
            if (level.getValue().equals(course.getLevel())) {
                dto.setLevelName(level.getLabel());
                break;
            }
        }

        // 设置状态名称
        for (Course.Status status : Course.Status.values()) {
            if (status.getValue().equals(course.getStatus())) {
                dto.setStatusName(status.getLabel());
                break;
            }
        }

        return dto;
    }
}