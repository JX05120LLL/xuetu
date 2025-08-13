package com.star.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.course.entity.Lesson;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课时数据访问层
 * 
 * @author star
 */
@Mapper
public interface LessonMapper extends BaseMapper<Lesson> {

    /**
     * 计算课程总时长
     */
    @Select("SELECT COALESCE(SUM(l.duration), 0) FROM lesson l " +
            "INNER JOIN chapter c ON l.chapter_id = c.id " +
            "WHERE c.course_id = #{courseId}")
    Integer sumDurationByCourseId(@Param("courseId") Long courseId);

    /**
     * 获取章节下的最大排序值
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) FROM lesson WHERE chapter_id = #{chapterId}")
    Integer getMaxSortOrderByChapterId(@Param("chapterId") Long chapterId);
}