package com.star.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项信息DTO
 * 
 * @author star
 */
@Data
@Schema(description = "订单项信息")
public class OrderItemDTO {

    @Schema(description = "订单项ID", example = "1")
    private Long id;

    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    @Schema(description = "课程标题", example = "Java基础入门教程")
    private String courseTitle;

    @Schema(description = "课程封面", example = "https://example.com/cover.jpg")
    private String courseCover;

    @Schema(description = "购买价格", example = "179.10")
    private BigDecimal price;

    @Schema(description = "购买数量", example = "1")
    private Integer quantity;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}