package com.star.ai.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 课程DTO（从course-service获取）
 * 
 * @author star
 */
@Data
public class CourseDTO implements Serializable {

    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private BigDecimal price;
    private Long categoryId;
    private Integer level;
    private Integer status;
    private Integer studentCount;
}
