package com.star.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.course.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 标签数据访问层
 * 
 * @author star
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据名称查询标签
     */
    @Select("SELECT * FROM tag WHERE name = #{name} LIMIT 1")
    Tag selectByName(@Param("name") String name);

    /**
     * 检查标签是否被课程使用
     */
    @Select("SELECT COUNT(*) FROM course_tag WHERE tag_id = #{tagId}")
    Integer countUsageByTagId(@Param("tagId") Long tagId);

    /**
     * 根据课程ID查询标签列表
     */
    @Select("SELECT t.* FROM tag t " +
            "INNER JOIN course_tag ct ON t.id = ct.tag_id " +
            "WHERE ct.course_id = #{courseId} " +
            "ORDER BY ct.created_time")
    List<Tag> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询热门标签（按使用次数排序）
     */
    @Select("SELECT t.*, COUNT(ct.course_id) as usage_count " +
            "FROM tag t " +
            "LEFT JOIN course_tag ct ON t.id = ct.tag_id " +
            "GROUP BY t.id, t.name, t.created_time, t.updated_time " +
            "ORDER BY usage_count DESC, t.created_time DESC " +
            "LIMIT #{limit}")
    List<Tag> selectPopularTags(@Param("limit") Integer limit);

    /**
     * 统计标签使用次数
     */
    @Select("SELECT COUNT(*) FROM course_tag WHERE tag_id = #{tagId}")
    Integer countUsageCount(@Param("tagId") Long tagId);

    /**
     * 根据课程ID查询标签数量
     */
    @Select("SELECT COUNT(*) FROM course_tag WHERE course_id = #{courseId}")
    Integer countTagsByCourseId(@Param("courseId") Long courseId);
}