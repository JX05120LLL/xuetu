package com.star.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_record")
@Schema(description = "支付记录信息")
public class PaymentRecord extends BaseEntity {

    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @Schema(description = "支付单号", example = "PAY202501160001")
    private String paymentNo;

    @Schema(description = "支付方式(1:模拟支付,2:微信支付,3:支付宝)", example = "1")
    @TableField("payment_type")
    private Integer paymentType;

    @Schema(description = "支付金额", example = "179.10")
    @TableField("total_amount")
    private BigDecimal paymentAmount;

    @Schema(description = "支付状态(0:待支付,1:支付成功,2:支付失败,3:已退款)", example = "1")
    @TableField("status")
    private Integer paymentStatus;

    @Schema(description = "第三方交易流水号", example = "TXN123456789")
    private String transactionId;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "回调时间")
    private LocalDateTime callbackTime;

    @Schema(description = "回调内容", example = "{\"status\":\"success\"}")
    private String callbackContent;

    @Schema(description = "支付备注", example = "模拟支付成功")
    private String remark;

    // 枚举定义
    public enum PaymentType {
        SIMULATION(1, "模拟支付"),
        WECHAT(2, "微信支付"),
        ALIPAY(3, "支付宝");

        private final Integer value;
        private final String label;

        PaymentType(Integer value, String label) {
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

    public enum PaymentStatus {
        PENDING(0, "待支付"),
        SUCCESS(1, "支付成功"),
        FAILED(2, "支付失败"),
        REFUNDED(3, "已退款");

        private final Integer value;
        private final String label;

        PaymentStatus(Integer value, String label) {
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