package com.star.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.course.entity.CourseTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;

/**
 * 课程标签关联数据访问层
 * 
 * @author star
 */
@Mapper
public interface CourseTagMapper extends BaseMapper<CourseTag> {

    /**
     * 删除课程的所有标签关联
     */
    @Delete("DELETE FROM course_tag WHERE course_id = #{courseId}")
    Integer deleteByCourseId(@Param("courseId") Long courseId);

    /**
     * 删除标签的所有课程关联
     */
    @Delete("DELETE FROM course_tag WHERE tag_id = #{tagId}")
    Integer deleteByTagId(@Param("tagId") Long tagId);

    /**
     * 删除特定的课程标签关联
     */
    @Delete("DELETE FROM course_tag WHERE course_id = #{courseId} AND tag_id = #{tagId}")
    Integer deleteByCourseIdAndTagId(@Param("courseId") Long courseId, @Param("tagId") Long tagId);
}