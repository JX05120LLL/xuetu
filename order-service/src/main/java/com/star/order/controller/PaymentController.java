package com.star.order.controller;

import com.star.common.result.R;
import com.star.order.dto.PaymentRecordDTO;
import com.star.order.dto.PaymentRequest;
import com.star.order.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 支付管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "支付管理", description = "支付相关接口")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "创建支付", description = "为订单创建支付记录")
    public R<PaymentRecordDTO> createPayment(@Valid @RequestBody PaymentRequest request,
                                             HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        PaymentRecordDTO payment = paymentService.createPayment(userId, request);
        return R.ok(payment, "支付创建成功");
    }

    @PostMapping("/{paymentNo}/simulate")
    @Operation(summary = "模拟支付", description = "模拟支付成功（开发测试用）")
    public R<PaymentRecordDTO> simulatePayment(@Parameter(description = "支付单号", required = true) 
                                               @PathVariable String paymentNo) {
        PaymentRecordDTO payment = paymentService.simulatePayment(paymentNo);
        return R.ok(payment, "模拟支付成功");
    }

    @GetMapping("/{paymentNo}")
    @Operation(summary = "查询支付记录", description = "根据支付单号查询支付记录")
    public R<PaymentRecordDTO> getPayment(@Parameter(description = "支付单号", required = true) 
                                          @PathVariable String paymentNo) {
        PaymentRecordDTO payment = paymentService.getPaymentByPaymentNo(paymentNo);
        return R.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "查询订单支付记录", description = "查询指定订单的所有支付记录")
    public R<List<PaymentRecordDTO>> getOrderPayments(@Parameter(description = "订单ID", required = true) 
                                                       @PathVariable Long orderId) {
        List<PaymentRecordDTO> payments = paymentService.getPaymentsByOrderId(orderId);
        return R.ok(payments);
    }

    @GetMapping("/{paymentNo}/status")
    @Operation(summary = "查询支付状态", description = "查询支付单的当前状态")
    public R<Integer> getPaymentStatus(@Parameter(description = "支付单号", required = true) 
                                       @PathVariable String paymentNo) {
        Integer status = paymentService.checkPaymentStatus(paymentNo);
        return R.ok(status);
    }

    @PostMapping("/{paymentNo}/refund")
    @Operation(summary = "申请退款", description = "为成功支付的订单申请退款")
    public R<Boolean> requestRefund(@Parameter(description = "支付单号", required = true) @PathVariable String paymentNo,
                                    @Parameter(description = "退款原因") @RequestParam(name = "reason", required = false) String reason) {
        Boolean result = paymentService.requestRefund(paymentNo, reason);
        return R.ok(result, "退款申请成功");
    }

    @PostMapping("/callback/{paymentNo}")
    @Operation(summary = "支付回调", description = "处理第三方支付平台的回调通知")
    public R<Boolean> paymentCallback(@Parameter(description = "支付单号", required = true) @PathVariable String paymentNo,
                                      @RequestBody String callbackData) {
        Boolean result = paymentService.handlePaymentCallback(paymentNo, callbackData);
        return R.ok(result, "回调处理成功");
    }

    /**
     * 快捷支付接口 - 创建支付后立即模拟支付成功
     */
    @PostMapping("/quick-pay")
    @Operation(summary = "快捷支付", description = "创建支付并立即模拟支付成功（测试用）")
    public R<PaymentRecordDTO> quickPay(@Valid @RequestBody PaymentRequest request,
                                        HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        
        // 1. 创建支付记录
        PaymentRecordDTO payment = paymentService.createPayment(userId, request);
        
        // 2. 立即模拟支付成功
        PaymentRecordDTO successPayment = paymentService.simulatePayment(payment.getPaymentNo());
        
        return R.ok(successPayment, "支付成功");
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("无效的用户ID: {}", userIdHeader);
            }
        }
        
        log.warn("未能从请求中获取用户ID，使用默认值");
        return 1L;
    }
}