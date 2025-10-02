package com.star.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.ai.entity.ChatHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 对话历史Mapper
 * 
 * @author star
 */
@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {

    /**
     * 查询用户的所有对话ID
     * 
     * @param userId 用户ID
     * @return 对话ID列表
     */
    @Select("SELECT conversation_id FROM (" +
            "  SELECT DISTINCT conversation_id, MAX(created_time) as max_time " +
            "  FROM chat_history " +
            "  WHERE user_id = #{userId} " +
            "  GROUP BY conversation_id " +
            "  ORDER BY max_time DESC " +
            "  LIMIT 20" +
            ") AS temp")
    List<String> selectUserConversations(Long userId);
}
