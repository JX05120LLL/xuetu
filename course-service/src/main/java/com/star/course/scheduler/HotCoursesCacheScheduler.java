package com.star.course.scheduler;

import com.star.common.utils.RedisUtil;
import com.star.course.dto.CourseDTO;
import com.star.course.entity.Course;
import com.star.course.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 热门课程缓存定时预热任务
 *
 * 作用：定期主动把热门课程数据刷新到 Redis，
 * 避免缓存集中过期时大量请求同时打到数据库（防雪崩）。
 *
 * @Scheduled 是 Spring 自带的定时任务注解，不需要额外依赖。
 * 需要在启动类上加 @EnableScheduling 才能生效。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HotCoursesCacheScheduler {

    private final CourseMapper courseMapper;
    private final RedisUtil redisUtil;

    // 热门课程缓存 key 前缀（和 CourseServiceImpl 保持一致）
    private static final String HOT_COURSES_CACHE_PREFIX = "course:hot:";

    // 预热的数量列表：预热 top10 和 top20 两档热门课程
    private static final int[] HOT_LIMITS = {10, 20};

    /**
     * 每 30 分钟执行一次热门课程缓存刷新。
     *
     * fixedDelay = 上一次执行完成后，再等 30 分钟才开始下一次。
     * 这样即使查询 DB 耗时较长，也不会出现并发执行的情况。
     *
     * 30分钟 = 30 * 60 * 1000 毫秒
     */
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void refreshHotCoursesCache() {
        log.info("【定时任务】开始刷新热门课程缓存...");

        for (int limit : HOT_LIMITS) {
            try {
                // 查数据库获取最新热门课程
                List<Course> courses = courseMapper.selectHotCourses(limit);
                List<CourseDTO> result = courses.stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                // 写入 Redis，TTL = 1小时 + 随机偏移（防雪崩）
                int ttl = 3600 + new Random().nextInt(300);
                String cacheKey = HOT_COURSES_CACHE_PREFIX + limit;
                redisUtil.set(cacheKey, result, ttl);

                log.info("【定时任务】热门课程缓存刷新成功，limit: {}, 数量: {}, TTL: {}秒",
                        limit, result.size(), ttl);

            } catch (Exception e) {
                // 定时任务出错不能影响主业务，只记录日志
                log.error("【定时任务】刷新热门课程缓存失败，limit: {}", limit, e);
            }
        }

        log.info("【定时任务】热门课程缓存刷新完成");
    }

    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        BeanUtils.copyProperties(course, dto);

        for (Course.Level level : Course.Level.values()) {
            if (level.getValue().equals(course.getLevel())) {
                dto.setLevelName(level.getLabel());
                break;
            }
        }
        for (Course.Status status : Course.Status.values()) {
            if (status.getValue().equals(course.getStatus())) {
                dto.setStatusName(status.getLabel());
                break;
            }
        }
        return dto;
    }
}
