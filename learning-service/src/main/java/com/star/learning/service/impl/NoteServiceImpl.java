package com.star.learning.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.common.dto.PageParam;
import com.star.common.utils.PageUtil;
import com.star.learning.dto.CreateNoteRequest;
import com.star.learning.dto.NoteDTO;
import com.star.learning.entity.Note;
import com.star.learning.exception.LearningServiceException;
import com.star.learning.mapper.NoteMapper;
import com.star.learning.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 笔记服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {

    private final NoteMapper noteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNote(Long userId, CreateNoteRequest request) {
        log.info("创建笔记: userId={}, courseId={}, lessonId={}, title={}", 
                userId, request.getCourseId(), request.getLessonId(), request.getTitle());

        Note note = new Note();
        BeanUtils.copyProperties(request, note);
        note.setUserId(userId);
        note.setCreatedTime(LocalDateTime.now());
        note.setUpdatedTime(LocalDateTime.now());

        boolean saved = save(note);
        if (!saved) {
            throw LearningServiceException.createFailed("创建笔记失败");
        }

        log.info("笔记创建成功: noteId={}", note.getId());
        return note.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateNote(Long userId, Long noteId, CreateNoteRequest request) {
        log.info("更新笔记: userId={}, noteId={}", userId, noteId);

        Note note = getById(noteId);
        if (note == null) {
            throw LearningServiceException.notFound("笔记不存在");
        }

        if (!note.getUserId().equals(userId)) {
            throw LearningServiceException.accessDenied("无权限操作此笔记");
        }

        BeanUtils.copyProperties(request, note);
        note.setUpdatedTime(LocalDateTime.now());

        boolean updated = updateById(note);
        if (!updated) {
            throw LearningServiceException.updateFailed("更新笔记失败");
        }

        log.info("笔记更新成功: noteId={}", noteId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteNote(Long userId, Long noteId) {
        log.info("删除笔记: userId={}, noteId={}", userId, noteId);

        Note note = getById(noteId);
        if (note == null) {
            throw LearningServiceException.notFound("笔记不存在");
        }

        if (!note.getUserId().equals(userId)) {
            throw LearningServiceException.accessDenied("无权限操作此笔记");
        }

        boolean deleted = removeById(noteId);
        if (!deleted) {
            throw LearningServiceException.deleteFailed("删除笔记失败");
        }

        log.info("笔记删除成功: noteId={}", noteId);
        return true;
    }

    @Override
    public NoteDTO getNoteDetail(Long userId, Long noteId) {
        log.info("查询笔记详情: userId={}, noteId={}", userId, noteId);

        Note note = getById(noteId);
        if (note == null) {
            throw LearningServiceException.notFound("笔记不存在");
        }

        if (!note.getUserId().equals(userId)) {
            throw LearningServiceException.accessDenied("无权限查看此笔记");
        }

        return convertToDTO(note);
    }

    @Override
    public IPage<NoteDTO> getUserNotesPage(Long userId, PageParam pageParam, String keyword) {
        log.info("分页查询用户笔记: userId={}, keyword={}", userId, keyword);

        Page<Note> page = PageUtil.buildPage(pageParam);
        IPage<Note> notePage = noteMapper.selectUserNotesPage(page, userId, keyword);

        return notePage.convert(this::convertToDTO);
    }

    @Override
    public List<NoteDTO> getCourseNotes(Long userId, Long courseId) {
        log.info("查询课程笔记: userId={}, courseId={}", userId, courseId);

        List<Note> notes = noteMapper.selectByUserIdAndCourseId(userId, courseId);
        return notes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NoteDTO> getLessonNotes(Long userId, Long lessonId) {
        log.info("查询课时笔记: userId={}, lessonId={}", userId, lessonId);

        List<Note> notes = noteMapper.selectByUserIdAndLessonId(userId, lessonId);
        return notes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NoteDTO> searchNotes(Long userId, String keyword, Integer limit) {
        log.info("搜索笔记: userId={}, keyword={}, limit={}", userId, keyword, limit);

        List<Note> notes = noteMapper.searchNotes(userId, keyword, limit);
        return notes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NoteDTO> getRecentNotes(Long userId, Integer limit) {
        log.info("查询最近笔记: userId={}, limit={}", userId, limit);

        List<Note> notes = noteMapper.selectRecentNotes(userId, limit);
        return notes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getUserNotesCount(Long userId) {
        return noteMapper.countByUserId(userId);
    }

    /**
     * 转换为DTO
     */
    private NoteDTO convertToDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        BeanUtils.copyProperties(note, dto);
        dto.setCreateTime(note.getCreatedTime());
        dto.setUpdateTime(note.getUpdatedTime());
        return dto;
    }
}