package com.star.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录信息DTO
 * 
 * @author star
 */
@Data
@Schema(description = "支付记录信息")
public class PaymentRecordDTO {

    @Schema(description = "支付记录ID", example = "1")
    private Long id;

    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @Schema(description = "支付单号", example = "PAY202501160001")
    private String paymentNo;

    @Schema(description = "支付方式", example = "1")
    private Integer paymentType;

    @Schema(description = "支付方式名称", example = "模拟支付")
    private String paymentTypeName;

    @Schema(description = "支付金额", example = "179.10")
    private BigDecimal paymentAmount;

    @Schema(description = "支付状态", example = "1")
    private Integer paymentStatus;

    @Schema(description = "支付状态名称", example = "支付成功")
    private String paymentStatusName;

    @Schema(description = "第三方交易流水号", example = "TXN123456789")
    private String transactionId;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "回调时间")
    private LocalDateTime callbackTime;

    @Schema(description = "支付备注", example = "模拟支付成功")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}