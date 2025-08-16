package com.star.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单信息DTO
 * 
 * @author star
 */
@Data
@Schema(description = "订单信息")
public class OrderDTO {

    @Schema(description = "订单ID", example = "1")
    private Long id;

    @Schema(description = "订单号", example = "ORD202501160001")
    private String orderNo;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "订单总金额", example = "199.00")
    private BigDecimal totalAmount;

    @Schema(description = "实际支付金额", example = "179.10")
    private BigDecimal actualAmount;

    @Schema(description = "优惠金额", example = "19.90")
    private BigDecimal discountAmount;

    @Schema(description = "订单状态", example = "1")
    private Integer status;

    @Schema(description = "订单状态名称", example = "已支付")
    private String statusName;

    @Schema(description = "订单备注", example = "用户购买Java基础课程")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "订单项列表")
    private List<OrderItemDTO> orderItems;

    @Schema(description = "支付记录列表")
    private List<PaymentRecordDTO> paymentRecords;
}