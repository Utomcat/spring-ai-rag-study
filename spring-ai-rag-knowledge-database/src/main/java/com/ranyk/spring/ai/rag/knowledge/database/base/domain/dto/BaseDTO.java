package com.ranyk.spring.ai.rag.knowledge.database.base.domain.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * CLASS_NAME: BaseDTO.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 数据传输通用公共类
 * @date: 2026-06-27
 */
@Data
@Slf4j
@SuperBuilder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class BaseDTO<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 6273770133752927645L;

    /**
     * 默认当前页码
     */
    private static final long DEFAULT_CURRENT_PAGE = 1L;
    /**
     * 默认分页大小
     */
    private static final long DEFAULT_PAGE_SIZE = 10L;


    // 以下是数据库的通用字段

    /**
     * 数据主键
     */
    private Long id;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 创建时间
     */
    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();
    /**
     * 更新人
     */
    private Long updateBy;
    /**
     * 更新时间
     */
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

    // 以下是分页参数的通用字段

    /**
     * 分页参数 - 数据总条数
     */
    private Long total;
    /**
     * 分页参数 - 当前页码
     */
    @Builder.Default
    private Integer page = 1;
    /**
     * 分页参数 - 每页显示条数
     */
    @Builder.Default
    private Integer size = 10;

    /**
     * 数据 List 集合
     */
    private List<T> dataList;

    /**
     * 构建分页对象
     *
     * @param dto 分页参数对象
     * @return 分页对象
     */
    public static <E> Page<E> buildPage(BaseDTO<?> dto) {
        // 判空处理
        if (dto == null) {
            return new Page<>(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE);
        }
        // 获取页码和页面大小，处理空值和非法值
        long currentPage = parseOrDefault(dto.getPage(), DEFAULT_CURRENT_PAGE);
        long pageSize = parseOrDefault(dto.getSize(), DEFAULT_PAGE_SIZE);
        return new Page<>(currentPage, pageSize);
    }

    /**
     * 将输入值解析为 Long 类型，若解析失败或为 null，则返回默认值
     *
     * @param value        输入值
     * @param defaultValue 默认值
     */
    private static long parseOrDefault(Object value, long defaultValue) {
        if (Objects.isNull(value)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            // 记录警告日志（可根据实际需求调整）
            log.error("Invalid numeric value: {}, using default: {}", value, defaultValue);
            return defaultValue;
        }
    }
}
