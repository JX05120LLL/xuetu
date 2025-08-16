package com.star.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程标签关联实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_tag")
@Schema(description = "课程标签关联信息")
public class CourseTag extends BaseEntity {

    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    @Schema(description = "标签ID", example = "1")
    private Long tagId;
}