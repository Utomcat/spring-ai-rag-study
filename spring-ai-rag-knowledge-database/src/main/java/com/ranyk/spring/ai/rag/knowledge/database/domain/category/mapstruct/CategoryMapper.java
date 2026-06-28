package com.ranyk.spring.ai.rag.knowledge.database.domain.category.mapstruct;

import com.ranyk.spring.ai.rag.knowledge.database.base.domain.po.PageQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.dto.CategoryDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.entity.Category;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.po.CategoryQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.po.CategorySavePO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.category.vo.CategoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Objects;

/**
 * CLASS_NAME: CategoryMapper.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库分类数据转换 MapStruct 接口
 * @date: 2026-06-28
 */
@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * 将 分页查询 PO 对象 {@link PageQueryPO} 转换为 知识库分类数据传输 DTO 对象 {@link CategoryDTO}
     *
     * @param pageQueryPO 分页查询 PO 对象 {@link PageQueryPO}
     * @return 知识库分类数据传输 DTO 对象 {@link CategoryDTO}
     */
    default CategoryDTO pageQueryPOToCategoryDTO(PageQueryPO<CategoryQueryPO> pageQueryPO) {
        if (Objects.isNull(pageQueryPO) || Objects.isNull(pageQueryPO.condition())) {
            return CategoryDTO.builder().build();
        }
        // 获取查询条件封装 PO 对象
        CategoryQueryPO condition = pageQueryPO.condition();
        // 构建 CategoryDTO 对象
        return CategoryDTO.builder()
                .name(condition.name())
                .description(condition.description())
                .icon(condition.icon())
                .sortOrder(condition.sortOrder())
                .size(pageQueryPO.size())
                .page(pageQueryPO.page())
                .build();
    }

    /**
     * 将 知识库分类数据传输 DTO 对象 {@link CategoryDTO} 转换为 知识库分类数据视图 VO 对象 {@link CategoryVO}
     *
     * @param categoryDTO 知识库分类数据传输 DTO 对象 {@link CategoryDTO}
     * @return 知识库分类数据视图 VO 对象 {@link CategoryVO}
     */
    CategoryVO categoryDTOToCategoryVO(CategoryDTO categoryDTO);

    /**
     * 将 知识库分类数据传输 DTO 对象列表 {@link CategoryDTO} 转换为 知识库分类数据视图 VO 对象列表 {@link CategoryVO}
     *
     * @param categoryDTOList 知识库分类数据传输 DTO 对象列表 {@link CategoryDTO}
     * @return 知识库分类数据视图 VO 对象列表 {@link CategoryVO}
     */
    List<CategoryVO> categoryDTOListToCategoryVOList(List<CategoryDTO> categoryDTOList);

    /**
     * 将 知识库分类数据保存 PO 对象 {@link CategorySavePO} 转换为 知识库分类数据传输 DTO 对象 {@link CategoryDTO}
     *
     * @param categorySavePO 知识库分类数据保存 PO 对象 {@link CategorySavePO}
     * @return 知识库分类数据传输 DTO 对象 {@link CategoryDTO}
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createBy", ignore = true),
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "updateBy", ignore = true),
            @Mapping(target = "updateTime", ignore = true),
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "total", ignore = true)
    })
    CategoryDTO categorySavePOToCategoryDTO(CategorySavePO categorySavePO);

    /**
     * 将 知识库分类数据保存 PO 对象列表 {@link CategorySavePO} 转换为 知识库分类数据传输 DTO 对象列表 {@link CategoryDTO}
     *
     * @param categorySavePOList 知识库分类数据保存 PO 对象列表 {@link CategorySavePO}
     * @return 知识库分类数据传输 DTO 对象列表 {@link CategoryDTO}
     */
    List<CategoryDTO> categorySavePOListToCategoryDTOList(List<CategorySavePO> categorySavePOList);

    /**
     * 将 知识库分类数据实体对象 {@link Category} 转换为 知识库分类数据传输 DTO 对象 {@link CategoryDTO}
     *
     * @param category 知识库分类数据实体对象 {@link Category}
     * @return 知识库分类数据传输 DTO 对象 {@link CategoryDTO}
     */
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "description", source = "description"),
            @Mapping(target = "icon", source = "icon"),
            @Mapping(target = "sortOrder", source = "sortOrder"),
            @Mapping(target = "createBy", source = "createBy"),
            @Mapping(target = "createTime", source = "createTime"),
            @Mapping(target = "updateBy", source = "updateBy"),
            @Mapping(target = "updateTime", source = "updateTime"),
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "total", ignore = true)
    })
    CategoryDTO categoryToCategoryDTO(Category category);

    /**
     * 将 知识库分类数据实体对象列表 {@link Category} 转换为 知识库分类数据传输 DTO 对象列表 {@link CategoryDTO}
     *
     * @param categoryList 知识库分类数据实体对象列表 {@link Category}
     * @return 知识库分类数据传输 DTO 对象列表 {@link CategoryDTO}
     */
    List<CategoryDTO> categoryListToCategoryDTOList(List<Category> categoryList);

    /**
     * 将 知识库分类数据传输 DTO 对象 {@link CategoryDTO} 转换为 知识库分类数据实体对象 {@link Category}
     *
     * @param categoryDTO 知识库分类数据传输 DTO 对象 {@link CategoryDTO}
     * @return 知识库分类数据实体对象 {@link Category}
     */
    Category categoryDTOToCategory(CategoryDTO categoryDTO);
}
