package com.star.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程分类实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
@Schema(description = "课程分类信息")
public class Category extends BaseEntity {

    @Schema(description = "分类名称", example = "Java编程")
    private String name;

    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;
}