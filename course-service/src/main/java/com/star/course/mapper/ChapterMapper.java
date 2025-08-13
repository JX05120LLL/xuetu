package com.star.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.course.entity.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 章节数据访问层
 * 
 * @author star
 */
@Mapper
public interface ChapterMapper extends BaseMapper<Chapter> {

    /**
     * 获取课程下的最大排序值
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) FROM chapter WHERE course_id = #{courseId}")
    Integer getMaxSortOrderByCourseId(@Param("courseId") Long courseId);
}