package com.star.common.dto;

import com.star.common.constant.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页参数
 * @author star
 */
@Data
@Schema(description = "分页参数")
public class PageParam {

    /**
     * 页码，从1开始
     */
    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码最小值为1")
    private Integer current = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "20")
    @Min(value = 1, message = "每页大小最小值为1")
    @Max(value = CommonConstant.MAX_PAGE_SIZE, message = "每页大小最大值为" + CommonConstant.MAX_PAGE_SIZE)
    private Integer size = CommonConstant.DEFAULT_PAGE_SIZE;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "created_time")
    private String orderBy;

    /**
     * 排序方式：asc-升序，desc-降序
     */
    @Schema(description = "排序方式", example = "desc")
    private String order = "desc";
}