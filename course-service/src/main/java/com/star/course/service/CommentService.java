package com.star.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.star.common.dto.PageParam;
import com.star.course.dto.CommentDTO;
import com.star.course.dto.CommentRequest;
import com.star.course.dto.ReplyDTO;
import com.star.course.dto.ReplyRequest;
import com.star.course.entity.Comment;
import com.star.course.mapper.CommentMapper;

import java.util.List;

/**
 * 评论服务接口
 * 
 * @author star
 */
public interface CommentService extends IService<Comment> {

    /**
     * 分页查询课程评论
     * 
     * @param pageParam 分页参数
     * @param courseId 课程ID
     * @return 评论分页数据
     */
    IPage<CommentDTO> getCommentPage(PageParam pageParam, Long courseId);

    /**
     * 查询评论详情
     * 
     * @param id 评论ID
     * @return 评论详情
     */
    CommentDTO getCommentDetail(Long id);

    /**
     * 创建评论
     * 
     * @param userId 用户ID
     * @param request 评论请求
     * @return 评论ID
     */
    Long createComment(Long userId, CommentRequest request);

    /**
     * 删除评论
     * 
     * @param id 评论ID
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean deleteComment(Long id, Long userId);

    /**
     * 点赞/取消点赞评论
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean likeComment(Long commentId, Long userId);

    /**
     * 创建回复
     * 
     * @param userId 用户ID
     * @param request 回复请求
     * @return 回复ID
     */
    Long createReply(Long userId, ReplyRequest request);

    /**
     * 删除回复
     * 
     * @param replyId 回复ID
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean deleteReply(Long replyId, Long userId);

    /**
     * 点赞/取消点赞回复
     * 
     * @param replyId 回复ID
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean likeReply(Long replyId, Long userId);

    /**
     * 查询评论的回复列表
     * 
     * @param commentId 评论ID
     * @return 回复列表
     */
    List<ReplyDTO> getRepliesByCommentId(Long commentId);

    /**
     * 获取课程评论统计信息
     * 
     * @param courseId 课程ID
     * @return 统计信息
     */
    CommentMapper.CommentStatistics getCommentStatistics(Long courseId);
}