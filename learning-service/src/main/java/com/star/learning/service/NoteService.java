package com.star.learning.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.star.common.dto.PageParam;
import com.star.learning.dto.CreateNoteRequest;
import com.star.learning.dto.NoteDTO;

import java.util.List;

/**
 * 笔记服务接口
 * 
 * @author star
 */
public interface NoteService {

    /**
     * 创建笔记
     */
    Long createNote(Long userId, CreateNoteRequest request);

    /**
     * 更新笔记
     */
    Boolean updateNote(Long userId, Long noteId, CreateNoteRequest request);

    /**
     * 删除笔记
     */
    Boolean deleteNote(Long userId, Long noteId);

    /**
     * 获取笔记详情
     */
    NoteDTO getNoteDetail(Long userId, Long noteId);

    /**
     * 分页查询用户笔记
     */
    IPage<NoteDTO> getUserNotesPage(Long userId, PageParam pageParam, String keyword);

    /**
     * 查询课程笔记
     */
    List<NoteDTO> getCourseNotes(Long userId, Long courseId);

    /**
     * 查询课时笔记
     */
    List<NoteDTO> getLessonNotes(Long userId, Long lessonId);

    /**
     * 搜索笔记
     */
    List<NoteDTO> searchNotes(Long userId, String keyword, Integer limit);

    /**
     * 获取最近笔记
     */
    List<NoteDTO> getRecentNotes(Long userId, Integer limit);

    /**
     * 获取用户笔记总数
     */
    Integer getUserNotesCount(Long userId);
}