/**
 * 评论相关类型定义
 */

/**
 * 评论DTO
 */
export interface CommentDTO {
  id: number
  courseId: number
  userId: number
  username?: string
  avatar?: string
  content: string
  rating: number
  likeCount: number
  replyCount: number
  createdTime: string
  updatedTime: string
  replies?: ReplyDTO[]
  isLiked?: boolean  // 当前用户是否点赞
  canDelete?: boolean  // 当前用户是否可以删除
}

/**
 * 回复DTO
 */
export interface ReplyDTO {
  id: number
  commentId: number
  userId: number
  username?: string
  avatar?: string
  replyToUserId?: number
  replyToUsername?: string
  content: string
  likeCount: number
  createdTime: string
  isLiked?: boolean  // 当前用户是否点赞
  canDelete?: boolean  // 当前用户是否可以删除
}

/**
 * 创建评论请求
 */
export interface CommentRequest {
  courseId: number
  content: string
  rating: number  // 评分：1-5星
}

/**
 * 创建回复请求
 */
export interface ReplyRequest {
  commentId: number
  content: string
  replyToUserId?: number  // 回复给谁（如果是回复评论则为空，回复回复则填写）
}

/**
 * 评论统计
 */
export interface CommentStatistics {
  totalCount: number
  avgRating: number
  ratingDistribution: {
    [key: number]: number  // 如 {5: 100, 4: 50, 3: 20, 2: 10, 1: 5}
  }
}
