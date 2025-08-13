package com.star.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.course.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类数据访问层
 * 
 * @author star
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据名称查询分类
     */
    @Select("SELECT * FROM category WHERE name = #{name} LIMIT 1")
    Category selectByName(@Param("name") String name);

    /**
     * 检查分类下是否有课程
     */
    @Select("SELECT COUNT(*) FROM course WHERE category_id = #{categoryId}")
    Integer countCoursesByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 检查分类下是否有子分类
     */
    @Select("SELECT COUNT(*) FROM category WHERE parent_id = #{categoryId}")
    Integer countChildrenByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 获取最大排序值
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) FROM category WHERE parent_id = #{parentId}")
    Integer getMaxSortOrder(@Param("parentId") Long parentId);
}