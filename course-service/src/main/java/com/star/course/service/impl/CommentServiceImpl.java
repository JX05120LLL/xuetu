package com.star.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.common.dto.PageParam;
import com.star.common.utils.PageUtil;
import com.star.course.dto.CommentDTO;
import com.star.course.dto.CommentRequest;
import com.star.course.dto.ReplyDTO;
import com.star.course.dto.ReplyRequest;
import com.star.course.entity.Comment;
import com.star.course.entity.CommentLike;
import com.star.course.entity.Reply;
import com.star.course.exception.CommentException;
import com.star.course.exception.CourseServiceException;
import com.star.course.exception.ReplyException;
import com.star.course.mapper.CommentLikeMapper;
import com.star.course.mapper.CommentMapper;
import com.star.course.mapper.ReplyMapper;
import com.star.course.service.CommentService;
import com.star.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final CommentMapper commentMapper;
    private final ReplyMapper replyMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final CourseService courseService;

    @Override
    public IPage<CommentDTO> getCommentPage(PageParam pageParam, Long courseId) {
        log.info("分页查询课程评论: courseId={}, current={}, size={}", 
                courseId, pageParam.getCurrent(), pageParam.getSize());

        Page<Comment> page = PageUtil.buildPage(pageParam);
        IPage<Comment> commentPage = commentMapper.selectCommentPageWithUser(page, courseId);

        return commentPage.convert(comment -> {
            CommentDTO dto = convertToDTO(comment);
            // 查询回复列表
            List<ReplyDTO> replies = getRepliesByCommentId(comment.getId());
            dto.setReplies(replies);
            return dto;
        });
    }

    @Override
    public CommentDTO getCommentDetail(Long id) {
        log.info("查询评论详情: id={}", id);

        Comment comment = getById(id);
        if (comment == null) {
            throw CommentException.notFound(id);
        }

        CommentDTO dto = convertToDTO(comment);
        // 查询回复列表
        List<ReplyDTO> replies = getRepliesByCommentId(id);
        dto.setReplies(replies);

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComment(Long userId, CommentRequest request) {
        log.info("创建评论: userId={}, courseId={}", userId, request.getCourseId());

        // 验证课程是否存在
        if (courseService.getById(request.getCourseId()) == null) {
            throw CourseServiceException.paramError("课程不存在: " + request.getCourseId());
        }

        // 验证评分范围
        if (request.getRating() != null && (request.getRating() < 1 || request.getRating() > 5)) {
            throw CommentException.invalidRating(request.getRating());
        }

        // 创建评论实体
        Comment comment = new Comment();
        BeanUtils.copyProperties(request, comment);
        comment.setUserId(userId);
        comment.setLikeCount(0);
        comment.setStatus(Comment.Status.NORMAL.getValue());
        comment.setIsTeacher(0);
        comment.setIsTop(0);
        comment.setCreatedTime(LocalDateTime.now());
        comment.setUpdatedTime(LocalDateTime.now());

        // 保存评论
        if (!save(comment)) {
            throw CourseServiceException.internalError("创建评论失败");
        }

        log.info("评论创建成功: id={}", comment.getId());
        return comment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteComment(Long id, Long userId) {
        log.info("删除评论: id={}, userId={}", id, userId);

        Comment comment = getById(id);
        if (comment == null) {
            throw CommentException.notFound(id);
        }

        // 只能删除自己的评论
        if (!comment.getUserId().equals(userId)) {
            throw CommentException.permissionDenied("删除他人评论");
        }

        // 软删除，设置状态为隐藏
        comment.setStatus(Comment.Status.HIDDEN.getValue());
        comment.setUpdatedTime(LocalDateTime.now());

        boolean success = updateById(comment);
        if (!success) {
            throw CourseServiceException.internalError("删除评论失败");
        }

        log.info("评论删除成功: id={}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean likeComment(Long commentId, Long userId) {
        log.info("点赞/取消点赞评论: commentId={}, userId={}", commentId, userId);

        Comment comment = getById(commentId);
        if (comment == null) {
            throw CommentException.notFound(commentId);
        }

        // 检查是否已点赞
        LambdaQueryWrapper<CommentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentLike::getUserId, userId)
                   .eq(CommentLike::getCommentId, commentId)
                   .eq(CommentLike::getType, CommentLike.Type.COMMENT.getValue());

        CommentLike existingLike = commentLikeMapper.selectOne(queryWrapper);

        if (existingLike != null) {
            // 取消点赞
            commentLikeMapper.deleteById(existingLike);
            commentMapper.updateLikeCount(commentId, -1);
            log.info("取消点赞评论成功");
        } else {
            // 添加点赞
            CommentLike like = new CommentLike();
            like.setUserId(userId);
            like.setCommentId(commentId);
            like.setType(CommentLike.Type.COMMENT.getValue());
            like.setCreatedTime(LocalDateTime.now());
            like.setUpdatedTime(LocalDateTime.now());
            
            commentLikeMapper.insert(like);
            commentMapper.updateLikeCount(commentId, 1);
            log.info("点赞评论成功");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReply(Long userId, ReplyRequest request) {
        log.info("创建回复: userId={}, commentId={}", userId, request.getCommentId());

        // 验证评论是否存在
        Comment comment = getById(request.getCommentId());
        if (comment == null) {
            throw CommentException.notFound(request.getCommentId());
        }

        // 验证评论状态
        if (comment.getStatus().equals(Comment.Status.HIDDEN.getValue())) {
            throw CommentException.alreadyDeleted(request.getCommentId());
        }

        // 验证是否回复自己的评论
        if (comment.getUserId().equals(userId)) {
            throw ReplyException.cannotReplyToSelf();
        }

        // 创建回复实体
        Reply reply = new Reply();
        BeanUtils.copyProperties(request, reply);
        reply.setUserId(userId);
        reply.setLikeCount(0);
        reply.setStatus(Reply.Status.NORMAL.getValue());
        reply.setCreatedTime(LocalDateTime.now());
        reply.setUpdatedTime(LocalDateTime.now());

        // 保存回复
        if (replyMapper.insert(reply) <= 0) {
            throw CourseServiceException.internalError("创建回复失败");
        }

        log.info("回复创建成功: id={}", reply.getId());
        return reply.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteReply(Long replyId, Long userId) {
        log.info("删除回复: replyId={}, userId={}", replyId, userId);

        Reply reply = replyMapper.selectById(replyId);
        if (reply == null) {
            throw ReplyException.notFound(replyId);
        }

        // 只能删除自己的回复
        if (!reply.getUserId().equals(userId)) {
            throw ReplyException.permissionDenied("删除他人回复");
        }

        // 软删除
        reply.setStatus(Reply.Status.HIDDEN.getValue());
        reply.setUpdatedTime(LocalDateTime.now());

        boolean success = replyMapper.updateById(reply) > 0;
        if (!success) {
            throw CourseServiceException.internalError("删除回复失败");
        }

        log.info("回复删除成功: id={}", replyId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean likeReply(Long replyId, Long userId) {
        log.info("点赞/取消点赞回复: replyId={}, userId={}", replyId, userId);

        Reply reply = replyMapper.selectById(replyId);
        if (reply == null) {
            throw ReplyException.notFound(replyId);
        }

        // 检查是否已点赞
        LambdaQueryWrapper<CommentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentLike::getUserId, userId)
                   .eq(CommentLike::getCommentId, replyId)
                   .eq(CommentLike::getType, CommentLike.Type.REPLY.getValue());

        CommentLike existingLike = commentLikeMapper.selectOne(queryWrapper);

        if (existingLike != null) {
            // 取消点赞
            commentLikeMapper.deleteById(existingLike);
            replyMapper.updateLikeCount(replyId, -1);
            log.info("取消点赞回复成功");
        } else {
            // 添加点赞
            CommentLike like = new CommentLike();
            like.setUserId(userId);
            like.setCommentId(replyId);
            like.setType(CommentLike.Type.REPLY.getValue());
            like.setCreatedTime(LocalDateTime.now());
            like.setUpdatedTime(LocalDateTime.now());
            
            commentLikeMapper.insert(like);
            replyMapper.updateLikeCount(replyId, 1);
            log.info("点赞回复成功");
        }

        return true;
    }

    @Override
    public List<ReplyDTO> getRepliesByCommentId(Long commentId) {
        log.info("查询评论回复列表: commentId={}", commentId);

        List<Reply> replies = replyMapper.selectRepliesByCommentId(commentId);
        return replies.stream()
                .map(this::convertReplyToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentMapper.CommentStatistics getCommentStatistics(Long courseId) {
        log.info("获取课程评论统计: courseId={}", courseId);
        return commentMapper.selectCommentStatistics(courseId);
    }

    /**
     * 转换评论为DTO
     */
    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        BeanUtils.copyProperties(comment, dto);

        // 设置状态名称
        for (Comment.Status status : Comment.Status.values()) {
            if (status.getValue().equals(comment.getStatus())) {
                dto.setStatusName(status.getLabel());
                break;
            }
        }

        // TODO: 设置是否已点赞（需要当前用户信息）
        dto.setIsLiked(false);

        return dto;
    }

    /**
     * 转换回复为DTO
     */
    private ReplyDTO convertReplyToDTO(Reply reply) {
        ReplyDTO dto = new ReplyDTO();
        BeanUtils.copyProperties(reply, dto);

        // 设置状态名称
        for (Reply.Status status : Reply.Status.values()) {
            if (status.getValue().equals(reply.getStatus())) {
                dto.setStatusName(status.getLabel());
                break;
            }
        }

        // TODO: 设置是否已点赞（需要当前用户信息）
        dto.setIsLiked(false);

        return dto;
    }
}