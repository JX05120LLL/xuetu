package com.star.common.dto;

import com.star.common.constant.CommonConstant;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 分页参数
 * @author star
 */
@Data
public class PageParam {

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码最小值为1")
    private Integer current = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小最小值为1")
    @Max(value = 500, message = "每页大小最大值为500")
    private Integer size = CommonConstant.DEFAULT_PAGE_SIZE;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式：asc-升序，desc-降序
     */
    private String order = "desc";

    /**
     * 无参构造函数
     */
    public PageParam() {
    }

    /**
     * 带参构造函数
     */
    public PageParam(Integer current, Integer size) {
        this.current = current;
        this.size = size;
    }

    /**
     * 带排序的构造函数
     */
    public PageParam(Integer current, Integer size, String orderBy, String order) {
        this.current = current;
        this.size = size;
        this.orderBy = orderBy;
        this.order = order;
    }
}