package com.star.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.course.entity.Reply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 回复数据访问层
 * 
 * @author star
 */
@Mapper
public interface ReplyMapper extends BaseMapper<Reply> {

    /**
     * 查询评论的回复列表（带用户信息）
     */
    @Select("""
        SELECT r.*, 
               u1.username as username, 
               u1.avatar as user_avatar,
               u2.username as target_username 
        FROM reply r 
        LEFT JOIN user u1 ON r.user_id = u1.id 
        LEFT JOIN user u2 ON r.target_user_id = u2.id 
        WHERE r.comment_id = #{commentId} AND r.status = 1 
        ORDER BY r.created_time ASC
        """)
    List<Reply> selectRepliesByCommentId(@Param("commentId") Long commentId);

    /**
     * 更新回复点赞数
     */
    @Update("UPDATE reply SET like_count = like_count + #{increment} WHERE id = #{replyId}")
    Integer updateLikeCount(@Param("replyId") Long replyId, @Param("increment") Integer increment);

    /**
     * 统计评论的回复数量
     */
    @Select("SELECT COUNT(*) FROM reply WHERE comment_id = #{commentId} AND status = 1")
    Integer countRepliesByCommentId(@Param("commentId") Long commentId);
}