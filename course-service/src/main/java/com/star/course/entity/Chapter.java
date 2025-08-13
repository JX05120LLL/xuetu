package com.star.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程章节实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("chapter")
@Schema(description = "课程章节信息")
public class Chapter extends BaseEntity {

    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    @Schema(description = "章节标题", example = "第一章：Java基础语法")
    private String title;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;
}