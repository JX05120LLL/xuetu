package com.star.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.ai.entity.AiFaq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI FAQ Mapper
 */
@Mapper
public interface AiFaqMapper extends BaseMapper<AiFaq> {

    /**
     * 查询所有启用状态的 FAQ
     */
    @Select("SELECT * FROM ai_faq WHERE status = 1 ORDER BY id")
    List<AiFaq> selectAllEnabled();
}
