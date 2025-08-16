package com.star.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单项实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_item")
@Schema(description = "订单项信息")
public class OrderItem extends BaseEntity {

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


}