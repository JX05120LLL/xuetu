package com.star.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.common.dto.PageParam;
import com.star.common.utils.PageUtil;
import com.star.common.utils.RedisUtil;
import com.star.course.dto.*;
import com.star.course.entity.Course;
import com.star.course.exception.CourseNotFoundException;
import com.star.course.exception.CourseServiceException;
import com.star.course.mapper.CourseMapper;
import com.star.course.service.CategoryService;
import com.star.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
    private final RedisUtil redisUtil;
    private final RedissonClient redissonClient;
    private final CourseBloomFilterServiceImpl bloomFilterService;

    // 课程详情缓存 key 前缀，完整示例：course:info:123
    private static final String COURSE_DETAIL_CACHE_PREFIX = "course:info:";
    // 热门课程缓存 key 前缀，完整示例：course:hot:10
    private static final String HOT_COURSES_CACHE_PREFIX = "course:hot:";
    // 分布式锁 key 前缀，完整示例：lock:course:detail:123
    private static final String COURSE_DETAIL_LOCK_PREFIX = "lock:course:detail:";
    // 课程详情基础缓存时间（秒）
    private static final int COURSE_DETAIL_BASE_TTL = 600;
    // 热门课程基础缓存时间（秒）
    private static final int HOT_COURSES_BASE_TTL = 3600;

    @Override
    public IPage<CourseDTO> getCoursePage(PageParam pageParam, String title, Long categoryId, Integer status,
            Integer level) {
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

        // ① 布隆过滤器：拦截根本不存在的课程ID（防穿透）
        //    mightContain 返回 false = 一定不存在，直接拒绝，不查缓存也不查DB
        if (!bloomFilterService.mightContain(id)) {
            log.warn("【布隆过滤器拦截】课程ID不存在: {}", id);
            throw CourseNotFoundException.notFound(id);
        }

        // ② 查 Redis 缓存
        String cacheKey = COURSE_DETAIL_CACHE_PREFIX + id;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            log.info("【缓存命中】课程ID: {}", id);
            return (CourseDTO) cached;
        }

        // ③ 缓存未命中，加分布式锁（防击穿）
        //    防止同一时刻大量请求同时涌入DB查同一门热门课程
        String lockKey = COURSE_DETAIL_LOCK_PREFIX + id;
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;
        try {
            // 等待最多 3 秒获取锁
            // 等待是必要的：让后来的请求稍等一下，等第一个请求把缓存填好
            isLocked = lock.tryLock(3, -1, TimeUnit.SECONDS);

            // ④ 拿到锁后做「双重检查」
            //    原因：在等这 3 秒期间，可能另一个线程已经查完 DB 并填好缓存了
            //    双重检查可以避免重复查 DB
            cached = redisUtil.get(cacheKey);
            if (cached != null) {
                log.info("【锁内缓存命中】课程ID: {}", id);
                return (CourseDTO) cached;
            }

            // ⑤ 缓存还是没有（自己是第一个），去查数据库
            Course course = getById(id);
            if (course == null) {
                throw CourseNotFoundException.notFound(id);
            }

            CourseDTO dto = convertToDTO(course);

            // ⑥ 写入 Redis，TTL = 基础时间 + 随机偏移（防雪崩）
            //    加随机数让不同课程的缓存过期时间错开，避免大批 key 同时失效
            int ttl = COURSE_DETAIL_BASE_TTL + new Random().nextInt(60);
            redisUtil.set(cacheKey, dto, ttl);
            log.info("【缓存已写入】课程ID: {}, TTL: {}秒", id, ttl);

            return dto;

        } catch (InterruptedException e) {
            // 等锁过程中线程被中断（极少发生），降级直接查 DB
            Thread.currentThread().interrupt();
            log.warn("获取锁被中断，降级查询数据库, courseId: {}", id);
            Course course = getById(id);
            if (course == null) {
                throw CourseNotFoundException.notFound(id);
            }
            return convertToDTO(course);
        } finally {
            // ⑦ 释放锁（无论成功还是出异常，finally 里一定执行）
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
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

        // 新课程 ID 加入布隆过滤器，防止刚创建的课程被误判为不存在
        bloomFilterService.addCourseId(course.getId());

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

        // 先查 Redis 缓存
        String cacheKey = HOT_COURSES_CACHE_PREFIX + limit;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            log.info("【热门课程缓存命中】limit: {}", limit);
            return (List<CourseDTO>) cached;
        }

        // 缓存没有，查数据库
        List<Course> courses = courseMapper.selectHotCourses(limit);
        List<CourseDTO> result = courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 写入缓存，TTL = 基础1小时 + 随机偏移（防雪崩）
        int ttl = HOT_COURSES_BASE_TTL + new Random().nextInt(300);
        redisUtil.set(cacheKey, result, ttl);
        log.info("【热门课程缓存已写入】limit: {}, TTL: {}秒", limit, ttl);

        return result;
    }

    @Override
    public IPage<CourseDTO> searchCourses(PageParam pageParam, String keyword) {
        log.info("搜索课程: keyword={}", keyword);

        Page<Course> page = PageUtil.buildPage(pageParam);

        // 使用MyBatis Plus的QueryWrapper进行模糊搜索
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(StringUtils.hasText(keyword), wrapper -> wrapper.like(Course::getTitle, keyword)
                .or()
                .like(Course::getDescription, keyword)).eq(Course::getStatus, Course.Status.PUBLISHED.getValue())
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