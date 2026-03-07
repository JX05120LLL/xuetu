package com.star.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.star.common.dto.PageParam;
import com.star.common.result.R;
import com.star.course.dto.CommentDTO;
import com.star.course.dto.CommentRequest;
import com.star.course.dto.ReplyDTO;
import com.star.course.dto.ReplyRequest;
import com.star.course.mapper.CommentMapper;
import com.star.course.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 评论管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Validated
@Tag(name = "评论管理", description = "评论和回复管理相关接口")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/course/{courseId}")
    @Operation(summary = "分页查询课程评论", description = "分页查询指定课程的评论列表")
    public R<IPage<CommentDTO>> getCommentPage(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Parameter(description = "分页参数") @ModelAttribute PageParam pageParam) {
        IPage<CommentDTO> commentPage = commentService.getCommentPage(pageParam, courseId);
        return R.ok(commentPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询评论详情", description = "根据评论ID查询评论详细信息")
    public R<CommentDTO> getCommentDetail(
            @Parameter(description = "评论ID") @PathVariable Long id) {
        CommentDTO comment = commentService.getCommentDetail(id);
        return R.ok(comment);
    }

    @PostMapping
    @Operation(summary = "创建评论", description = "用户对课程进行评论")
    public R<Long> createComment(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid CommentRequest request) {
        Long commentId = commentService.createComment(userId, request);
        return R.ok(commentId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论", description = "删除指定的评论")
    public R<Boolean> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        Boolean result = commentService.deleteComment(id, userId);
        return R.ok(result);
    }

    @PostMapping("/{commentId}/like")
    @Operation(summary = "点赞/取消点赞评论", description = "对评论进行点赞或取消点赞")
    public R<Boolean> likeComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            @RequestHeader("X-User-Id") Long userId) {
        Boolean result = commentService.likeComment(commentId, userId);
        return R.ok(result);
    }

    @PostMapping("/reply")
    @Operation(summary = "创建回复", description = "回复评论")
    public R<Long> createReply(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid ReplyRequest request) {
        Long replyId = commentService.createReply(userId, request);
        return R.ok(replyId);
    }

    @DeleteMapping("/reply/{replyId}")
    @Operation(summary = "删除回复", description = "删除指定的回复")
    public R<Boolean> deleteReply(
            @Parameter(description = "回复ID") @PathVariable Long replyId,
            @RequestHeader("X-User-Id") Long userId) {
        Boolean result = commentService.deleteReply(replyId, userId);
        return R.ok(result);
    }

    @PostMapping("/reply/{replyId}/like")
    @Operation(summary = "点赞/取消点赞回复", description = "对回复进行点赞或取消点赞")
    public R<Boolean> likeReply(
            @Parameter(description = "回复ID") @PathVariable Long replyId,
            @RequestHeader("X-User-Id") Long userId) {
        Boolean result = commentService.likeReply(replyId, userId);
        return R.ok(result);
    }

    @GetMapping("/{commentId}/replies")
    @Operation(summary = "查询评论回复列表", description = "查询指定评论下的所有回复")
    public R<List<ReplyDTO>> getRepliesByCommentId(
            @Parameter(description = "评论ID") @PathVariable Long commentId) {
        List<ReplyDTO> replies = commentService.getRepliesByCommentId(commentId);
        return R.ok(replies);
    }

    @GetMapping("/course/{courseId}/statistics")
    @Operation(summary = "获取课程评论统计", description = "获取课程的评论统计信息")
    public R<CommentMapper.CommentStatistics> getCommentStatistics(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        CommentMapper.CommentStatistics statistics = commentService.getCommentStatistics(courseId);
        return R.ok(statistics);
    }
}