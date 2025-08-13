package com.star.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.course.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 评论数据访问层
 * 
 * @author star
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 分页查询课程评论（带用户信息）
     */
    @Select("""
        SELECT c.*, u.username, u.avatar as user_avatar 
        FROM comment c 
        LEFT JOIN user u ON c.user_id = u.id 
        WHERE c.course_id = #{courseId} AND c.status = 1 
        ORDER BY c.is_top DESC, c.created_time DESC
        """)
    IPage<Comment> selectCommentPageWithUser(Page<Comment> page, @Param("courseId") Long courseId);

    /**
     * 更新评论点赞数
     */
    @Update("UPDATE comment SET like_count = like_count + #{increment} WHERE id = #{commentId}")
    Integer updateLikeCount(@Param("commentId") Long commentId, @Param("increment") Integer increment);

    /**
     * 查询课程评论统计信息
     */
    @Select("""
        SELECT 
            COUNT(*) as total_count,
            AVG(CASE WHEN rating IS NOT NULL THEN rating END) as avg_rating,
            COUNT(CASE WHEN rating = 5 THEN 1 END) as five_star_count,
            COUNT(CASE WHEN rating = 4 THEN 1 END) as four_star_count,
            COUNT(CASE WHEN rating = 3 THEN 1 END) as three_star_count,
            COUNT(CASE WHEN rating = 2 THEN 1 END) as two_star_count,
            COUNT(CASE WHEN rating = 1 THEN 1 END) as one_star_count
        FROM comment 
        WHERE course_id = #{courseId} AND status = 1
        """)
    CommentStatistics selectCommentStatistics(@Param("courseId") Long courseId);

    /**
     * 评论统计信息内部类
     */
    class CommentStatistics {
        private Long totalCount;
        private Double avgRating;
        private Long fiveStarCount;
        private Long fourStarCount;
        private Long threeStarCount;
        private Long twoStarCount;
        private Long oneStarCount;

        // Getters and Setters
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        
        public Double getAvgRating() { return avgRating; }
        public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
        
        public Long getFiveStarCount() { return fiveStarCount; }
        public void setFiveStarCount(Long fiveStarCount) { this.fiveStarCount = fiveStarCount; }
        
        public Long getFourStarCount() { return fourStarCount; }
        public void setFourStarCount(Long fourStarCount) { this.fourStarCount = fourStarCount; }
        
        public Long getThreeStarCount() { return threeStarCount; }
        public void setThreeStarCount(Long threeStarCount) { this.threeStarCount = threeStarCount; }
        
        public Long getTwoStarCount() { return twoStarCount; }
        public void setTwoStarCount(Long twoStarCount) { this.twoStarCount = twoStarCount; }
        
        public Long getOneStarCount() { return oneStarCount; }
        public void setOneStarCount(Long oneStarCount) { this.oneStarCount = oneStarCount; }
    }
}