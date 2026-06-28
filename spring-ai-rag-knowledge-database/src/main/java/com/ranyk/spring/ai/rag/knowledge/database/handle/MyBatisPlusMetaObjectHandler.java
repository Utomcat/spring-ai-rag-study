package com.ranyk.spring.ai.rag.knowledge.database.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ranyk.spring.ai.rag.knowledge.database.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * CLASS_NAME: MyBatisPlusMetaObjectHandler.java
 *
 * @author ranyk
 * @version V1.0
 * @description: MyBatis Plus 自动填充监听处理器
 * @date: 2026-06-28
 */
@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入元对象字段填充（用于插入时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        Long userId = SecurityUtils.currentUser().getUserId();
        this.strictInsertFill(metaObject, "createBy", Long.class, userId);
        this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
    }

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        Long userId = SecurityUtils.currentUser().getUserId();
        this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
    }
}
