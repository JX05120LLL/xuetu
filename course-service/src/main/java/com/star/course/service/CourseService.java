package com.star.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.star.course.dto.*;
import com.star.course.entity.Course;
import com.star.common.dto.PageParam;

import java.util.List;

/**
 * 课程服务接口
 * 
 * @author star
 */
public interface CourseService extends IService<Course> {

    /**
     * 分页查询课程列表
     * 
     * @param pageParam 分页参数
     * @param title 课程标题（模糊查询）
     * @param categoryId 分类ID
     * @param status 状态
     * @param level 难度级别
     * @return 课程分页数据
     */
    IPage<CourseDTO> getCoursePage(PageParam pageParam, String title, Long categoryId, Integer status, Integer level);

    /**
     * 根据ID查询课程详情
     * 
     * @param id 课程ID
     * @return 课程详情
     */
    CourseDTO getCourseDetail(Long id);

    /**
     * 创建课程
     * 
     * @param request 创建请求
     * @return 课程ID
     */
    Long createCourse(CourseCreateRequest request);

    /**
     * 更新课程
     * 
     * @param id 课程ID
     * @param request 更新请求
     * @return 是否成功
     */
    Boolean updateCourse(Long id, CourseUpdateRequest request);

    /**
     * 删除课程
     * 
     * @param id 课程ID
     * @return 是否成功
     */
    Boolean deleteCourse(Long id);

    /**
     * 发布课程
     * 
     * @param id 课程ID
     * @return 是否成功
     */
    Boolean publishCourse(Long id);

    /**
     * 下架课程
     * 
     * @param id 课程ID
     * @return 是否成功
     */
    Boolean offlineCourse(Long id);

    /**
     * 查询热门课程列表
     * 
     * @param limit 数量限制
     * @return 热门课程列表
     */
    List<CourseDTO> getHotCourses(Integer limit);

    /**
     * 搜索课程
     * 
     * @param pageParam 分页参数
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    IPage<CourseDTO> searchCourses(PageParam pageParam, String keyword);

    /**
     * 增加课程学习人数
     * 
     * @param id 课程ID
     * @param increment 增量
     * @return 是否成功
     */
    Boolean increaseStudentCount(Long id, Integer increment);

    /**
     * 批量更新课程状态
     * 
     * @param ids 课程ID列表
     * @param status 新状态
     * @return 是否成功
     */
    Boolean batchUpdateStatus(List<Long> ids, Integer status);

    /**
     * 根据分类ID查询课程数量
     * 
     * @param categoryId 分类ID
     * @return 课程数量
     */
    Integer getCourseCountByCategoryId(Long categoryId);
}