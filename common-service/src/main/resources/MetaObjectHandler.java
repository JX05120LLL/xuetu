package com.star.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 元数据对象处理器
 * 自动填充创建时间和更新时间
 * @author star
 */
@Slf4j
@Component
public class MetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入时自动填充...");
        
        LocalDateTime now = LocalDateTime.now();
        
        // 自动填充创建时间
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, now);
        
        // 自动填充更新时间
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, now);
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新时自动填充...");
        
        // 自动填充更新时间
        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
    }
}