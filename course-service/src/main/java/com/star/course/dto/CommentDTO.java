package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论DTO
 * 
 * @author star
 */
@Data
@Schema(description = "评论信息")
public class CommentDTO {
    
    @Schema(description = "评论ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "用户头像")
    private String userAvatar;
    
    @Schema(description = "课程ID")
    private Long courseId;
    
    @Schema(description = "评论内容")
    private String content;
    
    @Schema(description = "评分(1-5)")
    private Integer rating;
    
    @Schema(description = "点赞数")
    private Integer likeCount;
    
    @Schema(description = "状态(0:隐藏,1:正常,2:待审核)")
    private Integer status;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "是否为讲师评论")
    private Integer isTeacher;
    
    @Schema(description = "是否置顶")
    private Integer isTop;
    
    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked;
    
    @Schema(description = "回复列表")
    private List<ReplyDTO> replies;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}