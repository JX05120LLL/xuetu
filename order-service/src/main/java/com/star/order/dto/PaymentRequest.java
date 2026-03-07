package com.star.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 支付请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "支付请求")
public class PaymentRequest {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号", example = "ORD202501160001", required = true)
    private String orderNo;

    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式(1:模拟支付,2:微信支付,3:支付宝)", example = "1", required = true)
    private Integer paymentType;

    @Schema(description = "支付备注", example = "用户支付Java课程")
    private String remark;
}