package com.star.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.common.constant.CommonConstant;
import com.star.common.dto.PageParam;
import com.star.common.dto.PageResult;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页工具类
 * @author star
 */
public class PageUtil {

    /**
     * 构建MyBatis Plus分页对象
     * @param pageParam 分页参数
     * @return 分页对象
     */
    public static <T> Page<T> buildPage(PageParam pageParam) {
        return buildPage(pageParam, null);
    }

    /**
     * 构建MyBatis Plus分页对象
     * @param pageParam 分页参数
     * @param entityClass 实体类
     * @return 分页对象
     */
    public static <T> Page<T> buildPage(PageParam pageParam, Class<T> entityClass) {
        int current = pageParam.getCurrent() != null ? pageParam.getCurrent() : CommonConstant.DEFAULT_CURRENT;
        int size = pageParam.getSize() != null ? pageParam.getSize() : CommonConstant.DEFAULT_PAGE_SIZE;
        
        // 限制分页大小
        if (size > CommonConstant.MAX_PAGE_SIZE) {
            size = CommonConstant.MAX_PAGE_SIZE;
        }
        
        Page<T> page = new Page<>(current, size);
        
        // 设置排序
        if (StringUtils.hasText(pageParam.getOrderBy())) {
            String order = pageParam.getOrder();
            if ("asc".equalsIgnoreCase(order)) {
                page.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.asc(pageParam.getOrderBy()));
            } else {
                page.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.desc(pageParam.getOrderBy()));
            }
        }
        
        return page;
    }

    /**
     * 转换为分页结果
     * @param page MyBatis Plus分页对象
     * @return 分页结果
     */
    public static <T> PageResult<T> toPageResult(IPage<T> page) {
        return PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
    }

    /**
     * 转换为分页结果（带数据转换）
     * @param page MyBatis Plus分页对象
     * @param converter 数据转换器
     * @return 分页结果
     */
    public static <T, R> PageResult<R> toPageResult(IPage<T> page, Function<T, R> converter) {
        List<R> records = page.getRecords().stream()
                .map(converter)
                .collect(Collectors.toList());
        
        return PageResult.of(
                records,
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
    }

    /**
     * 构建空分页结果
     * @return 空分页结果
     */
    public static <T> PageResult<T> emptyPageResult() {
        return PageResult.empty();
    }

    /**
     * 构建分页结果
     * @param records 数据列表
     * @param total 总数量
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    public static <T> PageResult<T> buildPageResult(List<T> records, Long total, Long current, Long size) {
        return PageResult.of(records, total, current, size);
    }

    /**
     * 计算总页数
     * @param total 总数量
     * @param size 每页大小
     * @return 总页数
     */
    public static Long calculateTotalPages(Long total, Long size) {
        if (total == null || total <= 0 || size == null || size <= 0) {
            return 0L;
        }
        return (total + size - 1) / size;
    }

    /**
     * 计算偏移量
     * @param current 当前页码
     * @param size 每页大小
     * @return 偏移量
     */
    public static Long calculateOffset(Long current, Long size) {
        if (current == null || current <= 0 || size == null || size <= 0) {
            return 0L;
        }
        return (current - 1) * size;
    }

    /**
     * 验证分页参数
     * @param pageParam 分页参数
     * @return 验证后的分页参数
     */
    public static PageParam validatePageParam(PageParam pageParam) {
        if (pageParam == null) {
            pageParam = new PageParam();
        }
        
        // 验证页码
        if (pageParam.getCurrent() == null || pageParam.getCurrent() < 1) {
            pageParam.setCurrent(CommonConstant.DEFAULT_CURRENT);
        }
        
        // 验证页大小
        if (pageParam.getSize() == null || pageParam.getSize() < 1) {
            pageParam.setSize(CommonConstant.DEFAULT_PAGE_SIZE);
        } else if (pageParam.getSize() > CommonConstant.MAX_PAGE_SIZE) {
            pageParam.setSize(CommonConstant.MAX_PAGE_SIZE);
        }
        
        // 验证排序方式
        if (!StringUtils.hasText(pageParam.getOrder()) || 
            (!"asc".equalsIgnoreCase(pageParam.getOrder()) && !"desc".equalsIgnoreCase(pageParam.getOrder()))) {
            pageParam.setOrder("desc");
        }
        
        return pageParam;
    }

    /**
     * 获取分页信息描述
     * @param pageResult 分页结果
     * @return 分页信息描述
     */
    public static <T> String getPageInfo(PageResult<T> pageResult) {
        if (pageResult == null) {
            return "无分页信息";
        }
        
        long start = (pageResult.getCurrent() - 1) * pageResult.getSize() + 1;
        long end = Math.min(start + pageResult.getSize() - 1, pageResult.getTotal());
        
        return String.format("第%d页，共%d页，显示第%d-%d条，共%d条记录",
                pageResult.getCurrent(),
                pageResult.getPages(),
                start,
                end,
                pageResult.getTotal());
    }
}