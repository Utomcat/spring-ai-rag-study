package com.ranyk.spring.ai.rag.knowledge.database.base.domain.po;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.drew.lang.annotations.NotNull;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.Objects;

/**
 * CLASS_NAME: PageQuery.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 通用的分页查询包装器 PO 类, 其字段说明如下:
 * <ul>
 *     <li>page: 当前页码</li>
 *     <li>size: 每页大小</li>
 *     <li>condition: 查询条件实体对象</li>
 *     <li>orderItems: 排序条件 List 集合, 单个参见 {@link OrderItem}</li>
 * </ul>
 * @date: 2026-06-28
 */
@SuppressWarnings("unused")
public record PageQueryPO<T>(@NotNull @Min(1) Integer page,
                             @NotNull @Min(10) Integer size,
                             T condition,
                             List<OrderItem> orderItems) {

    /**
     * {@link PageQueryPO} 构造方法 - 无任何参数的默认构造方法
     */
    public PageQueryPO {
        page = (Objects.isNull(page) || page < 1) ? 1 : page;
        size = (Objects.isNull(size) || size < 1) ? 10 : size;
    }

    /**
     * {@link PageQueryPO} 构造方法 - 无 排序条件属性
     *
     * @param page      当前页码
     * @param size      每页大小
     * @param condition 查询条件实体对象
     */
    public PageQueryPO(Integer page, Integer size, T condition) {
        this(page, size, condition, null);
    }

}
