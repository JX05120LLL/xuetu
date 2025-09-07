package com.star.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.learning.entity.Note;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 笔记Mapper
 * 
 * @author star
 */
@Mapper
public interface NoteMapper extends BaseMapper<Note> {

    /**
     * 分页查询用户笔记
     */
    IPage<Note> selectUserNotesPage(Page<Note> page, @Param("userId") Long userId, @Param("keyword") String keyword);

    /**
     * 根据课程ID查询用户笔记
     */
    List<Note> selectByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 根据课时ID查询用户笔记
     */
    List<Note> selectByUserIdAndLessonId(@Param("userId") Long userId, @Param("lessonId") Long lessonId);

    /**
     * 查询用户笔记总数
     */
    Integer countByUserId(@Param("userId") Long userId);

    /**
     * 查询最近的笔记
     */
    List<Note> selectRecentNotes(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 根据关键词搜索笔记
     */
    List<Note> searchNotes(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("limit") Integer limit);
}