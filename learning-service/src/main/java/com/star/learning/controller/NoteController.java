package com.star.learning.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.star.common.dto.PageParam;
import com.star.common.result.R;
import com.star.learning.dto.CreateNoteRequest;
import com.star.learning.dto.NoteDTO;
import com.star.learning.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 笔记控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/learning/note")
@RequiredArgsConstructor
@Tag(name = "学习笔记管理", description = "笔记的增删改查、搜索等功能")
public class NoteController {

    private final NoteService noteService;

    /**
     * 创建笔记
     */
    @PostMapping
    @Operation(summary = "创建笔记", description = "用户创建学习笔记")
    public R<Long> createNote(@Valid @RequestBody CreateNoteRequest request,
                             HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Long noteId = noteService.createNote(userId, request);
        return R.ok(noteId, "笔记创建成功");
    }

    /**
     * 更新笔记
     */
    @PutMapping("/{noteId}")
    @Operation(summary = "更新笔记", description = "更新笔记内容")
    public R<Boolean> updateNote(
            @Parameter(description = "笔记ID", required = true) @PathVariable Long noteId,
            @Valid @RequestBody CreateNoteRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean result = noteService.updateNote(userId, noteId, request);
        return R.ok(result, "笔记更新成功");
    }

    /**
     * 删除笔记
     */
    @DeleteMapping("/{noteId}")
    @Operation(summary = "删除笔记", description = "删除指定笔记")
    public R<Boolean> deleteNote(
            @Parameter(description = "笔记ID", required = true) @PathVariable Long noteId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean result = noteService.deleteNote(userId, noteId);
        return R.ok(result, "笔记删除成功");
    }

    /**
     * 获取笔记详情
     */
    @GetMapping("/{noteId}")
    @Operation(summary = "获取笔记详情", description = "查询指定笔记的详细信息")
    public R<NoteDTO> getNoteDetail(
            @Parameter(description = "笔记ID", required = true) @PathVariable Long noteId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        NoteDTO note = noteService.getNoteDetail(userId, noteId);
        return R.ok(note);
    }

    /**
     * 分页查询用户笔记
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询笔记", description = "分页查询用户的所有笔记")
    public R<IPage<NoteDTO>> getNotesPage(
            @Valid PageParam pageParam,
            @Parameter(description = "搜索关键词") @RequestParam(name = "keyword", required = false) String keyword,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        IPage<NoteDTO> result = noteService.getUserNotesPage(userId, pageParam, keyword);
        return R.ok(result);
    }

    /**
     * 获取我的笔记（分页）
     * 前端兼容接口
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的笔记", description = "分页查询用户的所有笔记")
    public R<IPage<NoteDTO>> getMyNotes(
            @Valid PageParam pageParam,
            @Parameter(description = "课程ID") @RequestParam(name = "courseId", required = false) Long courseId,
            @Parameter(description = "搜索关键词") @RequestParam(name = "keyword", required = false) String keyword,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        IPage<NoteDTO> result = noteService.getUserNotesPage(userId, pageParam, keyword);
        return R.ok(result);
    }

    /**
     * 获取课程笔记
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程笔记", description = "查询用户在某课程下的所有笔记")
    public R<List<NoteDTO>> getCourseNotes(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        List<NoteDTO> notes = noteService.getCourseNotes(userId, courseId);
        return R.ok(notes);
    }

    /**
     * 获取课时笔记
     */
    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "获取课时笔记", description = "查询用户在某课时下的所有笔记")
    public R<List<NoteDTO>> getLessonNotes(
            @Parameter(description = "课时ID", required = true) @PathVariable Long lessonId,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        List<NoteDTO> notes = noteService.getLessonNotes(userId, lessonId);
        return R.ok(notes);
    }

    /**
     * 搜索笔记
     */
    @GetMapping("/search")
    @Operation(summary = "搜索笔记", description = "根据关键词搜索用户笔记")
    public R<List<NoteDTO>> searchNotes(
            @Parameter(description = "搜索关键词", required = true) @RequestParam(name = "keyword") String keyword,
            @Parameter(description = "限制条数", example = "20") @RequestParam(name = "limit", defaultValue = "20") Integer limit,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        List<NoteDTO> notes = noteService.searchNotes(userId, keyword, limit);
        return R.ok(notes);
    }

    /**
     * 获取最近笔记
     */
    @GetMapping("/recent")
    @Operation(summary = "获取最近笔记", description = "查询用户最近创建的笔记")
    public R<List<NoteDTO>> getRecentNotes(
            @Parameter(description = "限制条数", example = "10") @RequestParam(name = "limit", defaultValue = "10") Integer limit,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        List<NoteDTO> notes = noteService.getRecentNotes(userId, limit);
        return R.ok(notes);
    }

    /**
     * 获取笔记统计
     */
    @GetMapping("/count")
    @Operation(summary = "获取笔记统计", description = "查询用户笔记总数")
    public R<Integer> getNotesCount(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Integer count = noteService.getUserNotesCount(userId);
        return R.ok(count);
    }

    /**
     * 从请求中获取用户ID
     * TODO: 实际项目中应该从JWT Token中解析
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 临时方案：从请求头中获取用户ID
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr != null) {
            try {
                return Long.valueOf(userIdStr);
            } catch (NumberFormatException e) {
                log.warn("用户ID格式错误: {}", userIdStr);
            }
        }
        
        // 默认返回测试用户ID
        return 1L;
    }
}