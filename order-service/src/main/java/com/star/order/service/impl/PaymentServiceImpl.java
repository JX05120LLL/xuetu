package com.star.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.order.dto.PaymentRecordDTO;
import com.star.order.dto.PaymentRequest;
import com.star.order.entity.Order;
import com.star.order.entity.PaymentRecord;
import com.star.order.exception.OrderException;
import com.star.order.exception.PaymentException;
import com.star.order.mapper.OrderMapper;
import com.star.order.mapper.PaymentRecordMapper;
import com.star.order.service.OrderService;
import com.star.order.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 支付服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements PaymentService {

    private final PaymentRecordMapper paymentRecordMapper;
    private final OrderMapper orderMapper;
    private final OrderService orderService;

    public PaymentServiceImpl(PaymentRecordMapper paymentRecordMapper,
                             OrderMapper orderMapper,
                             @Lazy OrderService orderService) {
        this.paymentRecordMapper = paymentRecordMapper;
        this.orderMapper = orderMapper;
        this.orderService = orderService;
    }

    // 支付单号生成器
    private static final AtomicLong PAYMENT_SEQUENCE = new AtomicLong(1);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentRecordDTO createPayment(Long userId, PaymentRequest request) {
        log.info("用户 {} 创建支付记录，订单号: {}", userId, request.getOrderNo());
        
        // 1. 查询订单
        Order order = orderMapper.selectByOrderNo(request.getOrderNo());
        if (order == null) {
            throw OrderException.notFoundByOrderNo(request.getOrderNo());
        }
        
        // 2. 验证订单归属
        if (!order.getUserId().equals(userId)) {
            throw OrderException.notBelongToUser(order.getId(), userId);
        }
        
        // 3. 验证订单状态
        if (!Order.Status.PENDING.getValue().equals(order.getStatus())) {
            throw OrderException.invalidStatus("支付", getOrderStatusName(order.getStatus()));
        }
        
        // 4. 检查是否已有成功支付记录
        PaymentRecord existingPayment = paymentRecordMapper.selectSuccessPaymentByOrderId(order.getId());
        if (existingPayment != null) {
            throw PaymentException.duplicatePayment(existingPayment.getPaymentNo());
        }
        
        // 5. 验证支付方式
        if (!isValidPaymentType(request.getPaymentType())) {
            throw PaymentException.unsupportedPaymentType(request.getPaymentType());
        }
        
        // 6. 创建支付记录
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setOrderId(order.getId());
        paymentRecord.setPaymentNo(generatePaymentNo());
        paymentRecord.setPaymentType(request.getPaymentType());
        paymentRecord.setPaymentAmount(order.getActualAmount());
        paymentRecord.setPaymentStatus(PaymentRecord.PaymentStatus.PENDING.getValue());
        paymentRecord.setRemark(request.getRemark());
        
        save(paymentRecord);
        
        log.info("支付记录创建成功，支付单号: {}", paymentRecord.getPaymentNo());
        
        return convertToPaymentRecordDTO(paymentRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentRecordDTO simulatePayment(String paymentNo) {
        log.info("开始模拟支付，支付单号: {}", paymentNo);
        
        // 1. 查询支付记录
        PaymentRecord paymentRecord = paymentRecordMapper.selectByPaymentNo(paymentNo);
        if (paymentRecord == null) {
            throw PaymentException.notFound(paymentNo);
        }
        
        // ===== 调试日志 =====
        log.info("【调试】支付记录详情 - ID: {}, 订单ID: {}, 支付状态值: {}, 支付类型: {}, 金额: {}", 
                 paymentRecord.getId(), 
                 paymentRecord.getOrderId(), 
                 paymentRecord.getPaymentStatus(),
                 paymentRecord.getPaymentType(),
                 paymentRecord.getPaymentAmount());
        log.info("【调试】PENDING枚举值: {}, 实际状态值: {}, 是否相等: {}", 
                 PaymentRecord.PaymentStatus.PENDING.getValue(),
                 paymentRecord.getPaymentStatus(),
                 PaymentRecord.PaymentStatus.PENDING.getValue().equals(paymentRecord.getPaymentStatus()));
        // ==================
        
        // 2. 验证支付状态
        if (!PaymentRecord.PaymentStatus.PENDING.getValue().equals(paymentRecord.getPaymentStatus())) {
            throw PaymentException.invalidStatus("支付", getPaymentStatusName(paymentRecord.getPaymentStatus()));
        }
        
        // 3. 模拟支付成功
        paymentRecord.setPaymentStatus(PaymentRecord.PaymentStatus.SUCCESS.getValue());
        paymentRecord.setTransactionId("SIM" + System.currentTimeMillis());
        paymentRecord.setPaymentTime(LocalDateTime.now());
        paymentRecord.setCallbackTime(LocalDateTime.now());
        paymentRecord.setCallbackContent("{\"status\":\"success\",\"message\":\"模拟支付成功\"}");
        paymentRecord.setRemark("模拟支付 - 自动成功");
        
        boolean updated = updateById(paymentRecord);
        if (!updated) {
            throw PaymentException.paymentFailed("更新支付记录失败");
        }
        
        // 4. 处理订单支付成功
        Order order = orderMapper.selectById(paymentRecord.getOrderId());
        if (order != null) {
            boolean orderHandled = orderService.handlePaymentSuccess(order.getOrderNo(), paymentNo);
            if (!orderHandled) {
                log.error("处理订单支付成功回调失败，订单号: {}", order.getOrderNo());
            }
        }
        
        log.info("模拟支付成功，支付单号: {}", paymentNo);
        
        return convertToPaymentRecordDTO(paymentRecord);
    }

    @Override
    public PaymentRecordDTO getPaymentByPaymentNo(String paymentNo) {
        PaymentRecord paymentRecord = paymentRecordMapper.selectByPaymentNo(paymentNo);
        if (paymentRecord == null) {
            throw PaymentException.notFound(paymentNo);
        }
        return convertToPaymentRecordDTO(paymentRecord);
    }

    @Override
    public List<PaymentRecordDTO> getPaymentsByOrderId(Long orderId) {
        List<PaymentRecord> paymentRecords = paymentRecordMapper.selectByOrderId(orderId);
        return paymentRecords.stream()
                .map(this::convertToPaymentRecordDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean handlePaymentCallback(String paymentNo, String callbackData) {
        log.info("处理支付回调，支付单号: {}, 回调数据: {}", paymentNo, callbackData);
        
        PaymentRecord paymentRecord = paymentRecordMapper.selectByPaymentNo(paymentNo);
        if (paymentRecord == null) {
            throw PaymentException.notFound(paymentNo);
        }
        
        // 解析回调数据并更新支付状态
        // 这里简化处理，实际应该根据不同支付方式解析回调数据
        paymentRecord.setPaymentStatus(PaymentRecord.PaymentStatus.SUCCESS.getValue());
        paymentRecord.setCallbackTime(LocalDateTime.now());
        paymentRecord.setCallbackContent(callbackData);
        paymentRecord.setPaymentTime(LocalDateTime.now());
        
        boolean updated = updateById(paymentRecord);
        if (updated) {
            // 处理订单支付成功
            Order order = orderMapper.selectById(paymentRecord.getOrderId());
            if (order != null) {
                orderService.handlePaymentSuccess(order.getOrderNo(), paymentNo);
            }
        }
        
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean requestRefund(String paymentNo, String reason) {
        log.info("申请退款，支付单号: {}, 原因: {}", paymentNo, reason);
        
        PaymentRecord paymentRecord = paymentRecordMapper.selectByPaymentNo(paymentNo);
        if (paymentRecord == null) {
            throw PaymentException.notFound(paymentNo);
        }
        
        if (!PaymentRecord.PaymentStatus.SUCCESS.getValue().equals(paymentRecord.getPaymentStatus())) {
            throw PaymentException.invalidStatus("退款", getPaymentStatusName(paymentRecord.getPaymentStatus()));
        }
        
        // 模拟退款成功
        paymentRecord.setPaymentStatus(PaymentRecord.PaymentStatus.REFUNDED.getValue());
        paymentRecord.setRemark(paymentRecord.getRemark() + " | 退款原因: " + reason);
        
        boolean updated = updateById(paymentRecord);
        if (updated) {
            // 更新订单状态为已退款
            Order order = orderMapper.selectById(paymentRecord.getOrderId());
            if (order != null) {
                order.setStatus(Order.Status.REFUNDED.getValue());
                orderMapper.updateById(order);
            }
        }
        
        log.info("退款处理完成，支付单号: {}", paymentNo);
        return updated;
    }

    @Override
    public Integer checkPaymentStatus(String paymentNo) {
        PaymentRecord paymentRecord = paymentRecordMapper.selectByPaymentNo(paymentNo);
        if (paymentRecord == null) {
            throw PaymentException.notFound(paymentNo);
        }
        return paymentRecord.getPaymentStatus();
    }

    /**
     * 生成支付单号
     */
    private String generatePaymentNo() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long sequence = PAYMENT_SEQUENCE.getAndIncrement() % 10000;
        return "PAY" + dateTime + String.format("%04d", sequence);
    }

    /**
     * 验证支付方式是否有效
     */
    private boolean isValidPaymentType(Integer paymentType) {
        for (PaymentRecord.PaymentType type : PaymentRecord.PaymentType.values()) {
            if (type.getValue().equals(paymentType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转换为PaymentRecordDTO
     */
    private PaymentRecordDTO convertToPaymentRecordDTO(PaymentRecord paymentRecord) {
        PaymentRecordDTO dto = new PaymentRecordDTO();
        BeanUtils.copyProperties(paymentRecord, dto);
        dto.setCreateTime(paymentRecord.getCreatedTime());
        dto.setPaymentTypeName(getPaymentTypeName(paymentRecord.getPaymentType()));
        dto.setPaymentStatusName(getPaymentStatusName(paymentRecord.getPaymentStatus()));
        return dto;
    }

    /**
     * 获取支付方式名称
     */
    private String getPaymentTypeName(Integer paymentType) {
        for (PaymentRecord.PaymentType type : PaymentRecord.PaymentType.values()) {
            if (type.getValue().equals(paymentType)) {
                return type.getLabel();
            }
        }
        return "未知支付方式";
    }

    /**
     * 获取支付状态名称
     */
    private String getPaymentStatusName(Integer paymentStatus) {
        for (PaymentRecord.PaymentStatus status : PaymentRecord.PaymentStatus.values()) {
            if (status.getValue().equals(paymentStatus)) {
                return status.getLabel();
            }
        }
        return "未知状态";
    }

    /**
     * 获取订单状态名称
     */
    private String getOrderStatusName(Integer status) {
        for (Order.Status s : Order.Status.values()) {
            if (s.getValue().equals(status)) {
                return s.getLabel();
            }
        }
        return "未知状态";
    }
}