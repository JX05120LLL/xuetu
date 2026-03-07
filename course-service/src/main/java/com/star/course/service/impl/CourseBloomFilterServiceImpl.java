package com.star.course.service.impl;

import com.star.course.entity.Course;
import com.star.course.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 课程布隆过滤器服务
 * <p>
 * 使用 Redis BitMap 实现简易布隆过滤器，提前拦截明显不存在的课程 ID，
 * 减少无效请求对数据库的访问，解决缓存穿透问题的第一道防线。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseBloomFilterServiceImpl {

    /**
     * Redis 中用于存储布隆过滤器的 key
     */
    private static final String BLOOM_REDIS_KEY = "bf:course:id";

    /**
     * 位图大小（bit 数量），可根据实际课程规模调整
     * 这里约等于 3,355 万 bit，约 4MB 内存
     */
    private static final long BIT_SIZE = 1L << 25;

    /**
     * 使用的哈希函数个数
     */
    private static final int HASH_FUNCTION_COUNT = 3;

    private final RedisTemplate<String, Object> redisTemplate;
    private final CourseMapper courseMapper;

    /**
     * 服务启动时初始化布隆过滤器，并将已有课程 ID 预热到过滤器中。
     */
    @PostConstruct
    public void init() {
        List<Long> courseIds = courseMapper.selectList(null).stream()
                .map(Course::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (Long courseId : courseIds) {
            addCourseId(courseId);
        }

        log.info("课程布隆过滤器初始化完成，预热课程数量: {}", courseIds.size());
    }

    /**
     * 将课程 ID 添加到布隆过滤器中（在新课程创建成功后调用）。
     *
     * @param courseId 课程 ID
     */
    public void addCourseId(Long courseId) {
        if (courseId == null) {
            return;
        }
        long[] positions = getHashPositions(courseId);
        for (long position : positions) {
            redisTemplate.opsForValue().setBit(BLOOM_REDIS_KEY, position, true);
        }
    }

    /**
     * 判断给定课程 ID 是否有可能存在。
     *
     * @param courseId 课程 ID
     * @return true：可能存在；false：一定不存在
     */
    public boolean mightContain(Long courseId) {
        if (courseId == null) {
            return false;
        }
        long[] positions = getHashPositions(courseId);
        for (long position : positions) {
            Boolean bit = redisTemplate.opsForValue().getBit(BLOOM_REDIS_KEY, position);
            if (bit == null || !bit) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算给定值在布隆过滤器中的多个哈希位置。
     */
    private long[] getHashPositions(Long value) {
        long[] positions = new long[HASH_FUNCTION_COUNT];
        long hash1 = hash(value, 0);
        long hash2 = hash(value, 1);
        for (int i = 0; i < HASH_FUNCTION_COUNT; i++) {
            long combined = hash1 + i * hash2;
            // 确保为正数并映射到位图范围内
            positions[i] = (combined & Long.MAX_VALUE) % BIT_SIZE;
        }
        return positions;
    }

    /**
     * 简单的哈希函数实现（基于 value 和 seed 混合）。
     */
    private long hash(Long value, int seed) {
        long h = value.hashCode() ^ (seed * 0x5bd1e995L);
        h ^= (h >>> 16);
        h *= 0x5bd1e995L;
        h ^= (h >>> 13);
        return h;
    }
}


