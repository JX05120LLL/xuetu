package com.star.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建订单请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "创建订单请求")
public class CreateOrderRequest {

    @NotEmpty(message = "课程ID列表不能为空")
    @Schema(description = "课程ID列表", required = true)
    private List<Long> courseIds;

    @Schema(description = "订单备注", example = "购买Java课程")
    private String remark;

    @Schema(description = "优惠券代码", example = "DISCOUNT20")
    private String couponCode;
}