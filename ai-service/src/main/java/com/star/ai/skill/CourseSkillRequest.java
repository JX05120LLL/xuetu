package com.star.ai.skill;

import lombok.Data;

/**
 * /skill/course/list 接口的请求体
 */
@Data
public class CourseSkillRequest {

    /** 难度等级：初级 | 中级 | 高级（传中文名，内部转换为 1/2/3） */
    private String level;

    /** 课程分类 ID，不传则不过滤 */
    private Long categoryId;

    /** 返回数量上限，默认 10 */
    private Integer limit = 10;
}
