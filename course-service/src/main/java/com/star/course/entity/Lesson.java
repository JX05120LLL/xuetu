package com.star.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程课时实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lesson")
@Schema(description = "课程课时信息")
public class Lesson extends BaseEntity {

    @Schema(description = "章节ID", example = "1")
    private Long chapterId;

    @Schema(description = "课时标题", example = "1.1 Java环境搭建")
    private String title;

    @Schema(description = "视频URL", example = "https://example.com/video.mp4")
    private String videoUrl;

    @Schema(description = "时长(分钟)", example = "30")
    private Integer duration;

    @Schema(description = "是否免费(0:否,1:是)", example = "1")
    private Integer isFree;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;
}