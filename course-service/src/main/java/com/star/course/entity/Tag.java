package com.star.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程标签实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tag")
@Schema(description = "课程标签信息")
public class Tag extends BaseEntity {

    @Schema(description = "标签名称", example = "Java")
    private String name;

    @Schema(description = "标签颜色", example = "#FF5722")
    private String color;
}