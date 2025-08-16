package com.star.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order")
@Schema(description = "订单信息")
public class Order extends BaseEntity {

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

    @Schema(description = "订单状态(0:待支付,1:已支付,2:已取消,3:已退款,4:已完成)", example = "1")
    private Integer status;

    @Schema(description = "订单备注", example = "用户购买Java基础课程")
    private String remark;

    // 枚举定义
    public enum Status {
        PENDING(0, "待支付"),
        PAID(1, "已支付"),
        CANCELLED(2, "已取消"),
        REFUNDED(3, "已退款"),
        COMPLETED(4, "已完成");

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