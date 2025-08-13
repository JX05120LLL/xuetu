package com.star.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.course.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 评论点赞数据访问层
 * 
 * @author star
 */
@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLike> {

    /**
     * 检查用户是否已点赞
     */
    @Select("SELECT COUNT(*) > 0 FROM comment_like WHERE user_id = #{userId} AND comment_id = #{commentId} AND type = #{type}")
    Boolean checkUserLiked(@Param("userId") Long userId, @Param("commentId") Long commentId, @Param("type") Integer type);
}