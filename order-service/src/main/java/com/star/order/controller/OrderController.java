package com.star.order.controller;

import com.star.common.dto.PageParam;
import com.star.common.dto.PageResult;
import com.star.common.result.R;
import com.star.order.dto.CreateOrderRequest;
import com.star.order.dto.OrderDTO;
import com.star.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 订单管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单", description = "用户创建新订单")
    public R<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request, 
                                   HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        OrderDTO order = orderService.createOrder(userId, request);
        return R.ok(order, "订单创建成功");
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "查询订单详情", description = "根据订单ID查询订单详细信息")
    public R<OrderDTO> getOrderDetail(@Parameter(description = "订单ID", required = true) 
                                      @PathVariable Long orderId) {
        OrderDTO order = orderService.getOrderDetail(orderId);
        return R.ok(order);
    }

    @GetMapping("/order-no/{orderNo}")
    @Operation(summary = "根据订单号查询订单", description = "根据订单号查询订单详细信息")
    public R<OrderDTO> getOrderByOrderNo(@Parameter(description = "订单号", required = true) 
                                         @PathVariable String orderNo) {
        OrderDTO order = orderService.getOrderDetailByOrderNo(orderNo);
        return R.ok(order);
    }

    @GetMapping("/my")
    @Operation(summary = "查询我的订单", description = "分页查询当前用户的订单列表，支持按状态筛选")
    public R<PageResult<OrderDTO>> getMyOrders(@Parameter(description = "页码") @RequestParam(name = "current", defaultValue = "1") Integer current,
                                               @Parameter(description = "每页大小") @RequestParam(name = "size", defaultValue = "10") Integer size,
                                               @Parameter(description = "订单状态（可选）") @RequestParam(name = "status", required = false) Integer status,
                                               HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        PageParam pageParam = new PageParam(current, size);
        
        // 如果指定了status，按状态查询；否则查询所有订单
        PageResult<OrderDTO> orders;
        if (status != null) {
            orders = orderService.getUserOrdersByStatus(userId, status, pageParam);
        } else {
            orders = orderService.getUserOrders(userId, pageParam);
        }
        
        return R.ok(orders);
    }

    @GetMapping("/my/status/{status}")
    @Operation(summary = "按状态查询我的订单", description = "分页查询当前用户指定状态的订单列表")
    public R<PageResult<OrderDTO>> getMyOrdersByStatus(@Parameter(description = "订单状态") @PathVariable Integer status,
                                                       @Parameter(description = "页码") @RequestParam(name = "current", defaultValue = "1") Integer current,
                                                       @Parameter(description = "每页大小") @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                       HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        PageParam pageParam = new PageParam(current, size);
        PageResult<OrderDTO> orders = orderService.getUserOrdersByStatus(userId, status, pageParam);
        return R.ok(orders);
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "取消指定的待支付订单")
    public R<Boolean> cancelOrder(@Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
                                  HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean result = orderService.cancelOrder(userId, orderId);
        return R.ok(result, "订单取消成功");
    }

    @PutMapping("/{orderId}/confirm")
    @Operation(summary = "确认收货", description = "确认收货（课程类订单可能不需要）")
    public R<Boolean> confirmOrder(@Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
                                   HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean result = orderService.confirmOrder(userId, orderId);
        return R.ok(result, "确认收货成功");
    }

    @GetMapping("/my/count")
    @Operation(summary = "统计我的订单数量", description = "统计当前用户的订单总数")
    public R<Integer> countMyOrders(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Integer count = orderService.countUserOrders(userId);
        return R.ok(count);
    }

    @PostMapping("/check-purchasable")
    @Operation(summary = "检查是否可购买", description = "检查用户是否可以购买指定课程")
    public R<Boolean> checkCanCreateOrder(@RequestBody CreateOrderRequest request,
                                          HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Boolean canCreate = orderService.checkCanCreateOrder(userId, request.getCourseIds());
        return R.ok(canCreate);
    }

    /**
     * 从请求中获取用户ID
     * 实际应该从JWT Token或请求头中获取
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 从请求头获取用户ID（由网关传递）
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("无效的用户ID: {}", userIdHeader);
            }
        }
        
        // 开发阶段返回默认用户ID
        log.warn("未能从请求中获取用户ID，使用默认值");
        return 1L;
    }
}