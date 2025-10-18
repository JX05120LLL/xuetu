import request from './request'
import type { PageParam, PageResult } from '@/types/common'
import type { CommentDTO, ReplyDTO, CommentRequest, ReplyRequest, CommentStatistics } from '@/types/comment'

/**
 * 分页查询课程评论
 */
export function getCourseComments(courseId: number, params: PageParam): Promise<PageResult<CommentDTO>> {
  return request({
    url: `/comment/course/${courseId}`,
    method: 'get',
    params
  })
}

/**
 * 查询评论详情
 */
export function getCommentDetail(id: number): Promise<CommentDTO> {
  return request({
    url: `/comment/${id}`,
    method: 'get'
  })
}

/**
 * 创建评论
 */
export function createComment(data: CommentRequest): Promise<number> {
  return request({
    url: '/comment',
    method: 'post',
    data
  })
}

/**
 * 删除评论
 */
export function deleteComment(id: number): Promise<boolean> {
  return request({
    url: `/comment/${id}`,
    method: 'delete'
  })
}

/**
 * 点赞/取消点赞评论
 */
export function likeComment(commentId: number): Promise<boolean> {
  return request({
    url: `/comment/${commentId}/like`,
    method: 'post'
  })
}

/**
 * 创建回复
 */
export function createReply(data: ReplyRequest): Promise<number> {
  return request({
    url: '/comment/reply',
    method: 'post',
    data
  })
}

/**
 * 删除回复
 */
export function deleteReply(replyId: number): Promise<boolean> {
  return request({
    url: `/comment/reply/${replyId}`,
    method: 'delete'
  })
}

/**
 * 点赞/取消点赞回复
 */
export function likeReply(replyId: number): Promise<boolean> {
  return request({
    url: `/comment/reply/${replyId}/like`,
    method: 'post'
  })
}

/**
 * 查询评论回复列表
 */
export function getRepliesByCommentId(commentId: number): Promise<ReplyDTO[]> {
  return request({
    url: `/comment/${commentId}/replies`,
    method: 'get'
  })
}

/**
 * 获取课程评论统计
 */
export function getCommentStatistics(courseId: number): Promise<CommentStatistics> {
  return request({
    url: `/comment/course/${courseId}/statistics`,
    method: 'get'
  })
}
