package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 回复DTO
 * 
 * @author star
 */
@Data
@Schema(description = "回复信息")
public class ReplyDTO {
    
    @Schema(description = "回复ID")
    private Long id;
    
    @Schema(description = "回复用户ID")
    private Long userId;
    
    @Schema(description = "回复用户名")
    private String username;
    
    @Schema(description = "回复用户头像")
    private String userAvatar;
    
    @Schema(description = "评论ID")
    private Long commentId;
    
    @Schema(description = "回复目标用户ID")
    private Long targetUserId;
    
    @Schema(description = "回复目标用户名")
    private String targetUsername;
    
    @Schema(description = "回复内容")
    private String content;
    
    @Schema(description = "点赞数")
    private Integer likeCount;
    
    @Schema(description = "状态(0:隐藏,1:正常)")
    private Integer status;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;
}