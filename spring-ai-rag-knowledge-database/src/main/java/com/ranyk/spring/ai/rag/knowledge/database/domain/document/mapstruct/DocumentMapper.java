package com.ranyk.spring.ai.rag.knowledge.database.domain.document.mapstruct;

import com.ranyk.spring.ai.rag.knowledge.database.base.domain.po.PageQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.dto.DocumentDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.entity.Document;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.po.DocumentQueryPO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.document.vo.DocumentVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Objects;

/**
 * CLASS_NAME: DocumentMapper.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 知识库文档数据转换 MapStruct 接口
 * @date: 2026-06-28
 */
@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public interface DocumentMapper {

    /**
     * 将 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 转换为 知识库文档数据访问 VO 对象 {@link DocumentVO}
     *
     * @param documentDTO 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     * @return 知识库文档数据访问 VO 对象 {@link DocumentVO}
     */
    DocumentVO documentDTOToDocumentVO(DocumentDTO documentDTO);

    /**
     * 将 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 列表转换为 知识库文档数据访问 VO 对象 {@link DocumentVO} 列表
     *
     * @param documentDTOs 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 列表
     * @return 知识库文档数据访问 VO 对象 {@link DocumentVO} 列表
     */
    List<DocumentVO> documentDTOListToDocumentVOList(List<DocumentDTO> documentDTOs);

    /**
     * 将 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 转换为 知识库文档数据访问 Entity 对象 {@link Document}
     *
     * @param documentDTO 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     * @return 知识库文档数据访问 Entity 对象 {@link Document}
     */
    Document documentDTOToDocument(DocumentDTO documentDTO);

    /**
     * 将分页查询参数对象 {@link PageQueryPO} 转换为 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     *
     * @param pageQueryPO 分页查询参数对象 {@link PageQueryPO}
     * @return 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     */
    default DocumentDTO pageQueryPOToDocumentDTO(PageQueryPO<DocumentQueryPO> pageQueryPO) {
        if (Objects.isNull(pageQueryPO) || Objects.isNull(pageQueryPO.condition())) {
            return DocumentDTO.builder().build();
        }

        DocumentQueryPO condition = pageQueryPO.condition();

        return DocumentDTO.builder()
                .keyword(condition.keyword())
                .categoryId(condition.categoryId())
                .size(pageQueryPO.size())
                .page(pageQueryPO.page())
                .build();
    }

    /**
     * 将 知识库文档数据访问 Entity 对象 {@link Document} 转换为 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     *
     * @param document 知识库文档数据访问 Entity 对象 {@link Document}
     * @return 知识库文档数据传输 DTO 对象 {@link DocumentDTO}
     */
    @Mappings({
            @Mapping(target = "dataList", ignore = true),
            @Mapping(target = "keyword", ignore = true),
            @Mapping(target = "page", ignore = true),
            @Mapping(target = "size", ignore = true),
            @Mapping(target = "total", ignore = true)
    })
    DocumentDTO documentToDocumentDTO(Document document);

    /**
     * 将 知识库文档数据访问 Entity 对象 {@link Document} 列表转换为 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 列表
     *
     * @param documentList 知识库文档数据访问 Entity 对象 {@link Document} 列表
     * @return 知识库文档数据传输 DTO 对象 {@link DocumentDTO} 列表
     */
    List<DocumentDTO> documentListToDocumentDTOList(List<Document> documentList);

}
