package com.star.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.course.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 课程数据访问层
 * 
 * @author star
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 查询热门课程列表（按学习人数排序）
     * 使用注解方式，无需映射文件
     */
    @Select("SELECT * FROM course WHERE status = 1 ORDER BY student_count DESC LIMIT #{limit}")
    List<Course> selectHotCourses(@Param("limit") Integer limit);

    /**
     * 更新课程学习人数（原子操作）
     * 使用注解方式，无需映射文件
     */
    @Update("UPDATE course SET student_count = student_count + #{increment} WHERE id = #{id}")
    Integer updateStudentCount(@Param("id") Long id, @Param("increment") Integer increment);

    /**
     * 根据分类ID统计课程数量
     */
    @Select("SELECT COUNT(*) FROM course WHERE category_id = #{categoryId}")
    Integer countCoursesByCategoryId(@Param("categoryId") Long categoryId);
}