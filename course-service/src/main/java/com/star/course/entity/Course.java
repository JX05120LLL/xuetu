package com.star.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课程实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
@Schema(description = "课程信息")
public class Course extends BaseEntity {

    @Schema(description = "课程标题", example = "Java基础入门教程")
    private String title;

    @Schema(description = "课程描述", example = "从零开始学习Java编程语言")
    private String description;

    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String coverImage;

    @Schema(description = "课程价格", example = "99.00")
    private BigDecimal price;

    @Schema(description = "原价", example = "199.00")
    private BigDecimal originalPrice;

    @Schema(description = "讲师ID", example = "1")
    private Long teacherId;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "难度级别(0:初级,1:中级,2:高级)", example = "0")
    private Integer level;

    @Schema(description = "状态(0:未发布,1:已发布,2:已下架)", example = "1")
    private Integer status;

    @Schema(description = "总时长(分钟)", example = "1200")
    private Integer totalDuration;

    @Schema(description = "学习人数", example = "520")
    private Integer studentCount;

    // 枚举定义
    public enum Level {
        BEGINNER(0, "初级"),
        INTERMEDIATE(1, "中级"),
        ADVANCED(2, "高级");

        private final Integer value;
        private final String label;

        Level(Integer value, String label) {
            this.value = value;
            this.label = label;
        }

        public Integer getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum Status {
        UNPUBLISHED(0, "未发布"),
        PUBLISHED(1, "已发布"),
        OFFLINE(2, "已下架");

        private final Integer value;
        private final String label;

        Status(Integer value, String label) {
            this.value = value;
            this.label = label;
        }

        public Integer getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }
}