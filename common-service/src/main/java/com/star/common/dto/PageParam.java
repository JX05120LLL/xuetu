package com.star.common.dto;

import com.star.common.constant.CommonConstant;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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
}