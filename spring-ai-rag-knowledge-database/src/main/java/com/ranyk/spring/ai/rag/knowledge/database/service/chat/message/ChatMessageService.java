package com.ranyk.spring.ai.rag.knowledge.database.service.chat.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ranyk.spring.ai.rag.knowledge.database.common.exception.ServiceException;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.dto.ChatMessageDTO;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.entity.ChatMessage;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.message.mapstruct.ChatMessageMapper;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.dto.ChatSessionDTO;
import com.ranyk.spring.ai.rag.knowledge.database.repository.chat.message.ChatMessageRepository;
import com.ranyk.spring.ai.rag.knowledge.database.service.chat.session.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CLASS_NAME: ChatMessageService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天消息业务逻辑处理类
 * @date: 2026-06-27
 */
@Slf4j
@Service
public class ChatMessageService extends ServiceImpl<ChatMessageRepository, ChatMessage> {

    /**
     * 聊天消息数据访问层接口
     */
    private final ChatMessageRepository chatMessageRepository;
    /**
     * 聊天会话业务逻辑处理类
     */
    private final ChatSessionService chatSessionService;
    /**
     * 向量存储器
     */
    private final VectorStore vectorStore;
    /**
     * RAG 大模型聊天客户端
     */
    private final ChatClient chatClient;
    /**
     * Jackson 对象映射器
     */
    private final ObjectMapper objectMapper;
    /**
     * RAG 算法中向量检索的 top_k 参数
     */
    private static final int RAG_TOP_K = 10;
    /**
     * 系统提示：要求仅依据上下文、Markdown 输出
     */
    public static final String SYSTEM_PROMPT = """
            
            你是「Ranyk RAG 企业知识库」的智能助手. 请严格根据检索到的上下文回答问题.
            
            若上下文不足以回答, 请明确说明「知识库中未找到相关信息」, 不要编造.
            
            回答请使用清晰的 Markdown（可适当使用标题、列表）. 结尾可简要列出依据的文档标题.
            
            """;
    private final ChatMessageMapper chatMessageMapper;

    /**
     * 构造函数 - 由 Spring IOC 容器创建当前 Bean 实例对象时,自动注入相关依赖的 Bean 实例对象
     *
     * @param chatMessageRepository 聊天消息数据访问层接口
     * @param chatSessionService    聊天会话业务逻辑处理类
     */
    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository,
                              ChatSessionService chatSessionService,
                              VectorStore vectorStore,
                              ChatClient chatClient,
                              ObjectMapper objectMapper, ChatMessageMapper chatMessageMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatSessionService = chatSessionService;
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
        this.chatMessageMapper = chatMessageMapper;
    }

    /**
     * 获取今日助手消息数量
     *
     * @return 今日助手消息数量
     */
    public Long countTodayAssistantMessages() {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getRole, "ASSISTANT")
                .ge(ChatMessage::getCreateTime, today.atStartOfDay())
                .lt(ChatMessage::getCreateTime, today.plusDays(1).atStartOfDay());
        return count(wrapper);
    }

    /**
     * 统计近7日每日助手回复消息数量
     *
     * @return 每日统计数据，包含 dayKey（日期）和 cnt（数量）
     */
    public List<Map<String, Long>> countAssistantByDayLast7() {
        return chatMessageRepository.countAssistantByDayLast7();
    }

    /**
     * 根据用户问题, 获取 RAG 算法生成的回复
     *
     * @param chatMessageDTO 聊天消息数据传输对象 {@link ChatMessageDTO}
     * @return 聊天消息数据传输对象 {@link ChatMessageDTO}
     */
    public ChatMessageDTO ask(ChatMessageDTO chatMessageDTO) {
        // 本次聊天的会话 ID
        Long sessionId = chatMessageDTO.getSessionId();
        // 不存在会话 ID, 则创建一个会话
        if (Objects.isNull(sessionId)) {
            String question = chatMessageDTO.getQuestion().trim();
            question = question.length() > 30 ? question.substring(0, 30) + "…" : question;
            ChatSessionDTO chatSessionDTO = chatSessionService.saveSessionInfo(ChatSessionDTO.builder().userId(chatMessageDTO.getUserId()).title(question).build());
            sessionId = chatSessionDTO.getId();
        } else {
            ChatSessionDTO chatSessionDTO = chatSessionService.queryById(ChatSessionDTO.builder().id(sessionId).build());
            if (Objects.isNull(chatSessionDTO) || !Objects.equals(chatSessionDTO.getUserId(), chatMessageDTO.getUserId())) {
                throw new ServiceException("sessions.no.permissions", new String[]{"会话不存在或无权限!"});
            }
        }
        long t0 = System.nanoTime();
        List<Document> cited = retrieveForCategories(chatMessageDTO.getQuestion(), chatMessageDTO.getCategoryIds());
        long retrievalMs = (System.nanoTime() - t0) / 1_000_000L;
        log.info("RAG 向量检索完成 sessionId={} 命中块数={} 耗时={}ms", sessionId, cited.size(), retrievalMs);
        String userTurn = buildRagUserMessage(chatMessageDTO.getQuestion(), cited);
        t0 = System.nanoTime();
        String answer = chatClient.prompt().system(SYSTEM_PROMPT).user(userTurn).call().content();
        long llmMs = (System.nanoTime() - t0) / 1_000_000L;
        log.info("LLM 生成完成 sessionId={} 耗时={}ms（SimpleLoggerAdvisor 将打出请求/响应摘要）", sessionId, llmMs);
        List<Map<String, Object>> refs = toRefs(cited);
        String refsJson;
        try {
            refsJson = objectMapper.writeValueAsString(refs);
        } catch (Exception e) {
            log.error("调用 ObjectsMapper.writeValueAsString 方法时发生异常, 异常信息为: {}", e.getMessage());
            throw new ServiceException("json.parse.error", new String[]{"调用 ObjectsMapper.writeValueAsString 方法时, 转换引用文档JSON, 发生异常"});
        }
        ChatMessage userChatMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .role("USER")
                .content(chatMessageDTO.getQuestion())
                .refs("-")
                .build();
        this.saveOrUpdate(userChatMessage);

        ChatMessage assistantChatMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .role("ASSISTANT")
                .content(answer)
                .refs(refsJson)
                .build();
        this.saveOrUpdate(assistantChatMessage);
        chatSessionService.touchUpdateTime(ChatSessionDTO.builder().id(sessionId).build());
        return ChatMessageDTO.builder().sessionId(sessionId).answer(answer).references(refs).build();
    }

    /**
     * 根据问题和类别 ID 列表，检索相关的文档
     *
     * @param question    问题
     * @param categoryIds 类别 ID 列表
     * @return 相关的文档列表
     */
    private List<Document> retrieveForCategories(String question, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return vectorSimilaritySearch(question, null);
        }
        Set<String> keys = categoryIds.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (keys.isEmpty()) {

            return vectorSimilaritySearch(question, null);

        }
        try {
            Filter.Expression expr = buildCategoryIdFilter(keys);
            List<Document> filtered = vectorSimilaritySearch(question, expr);
            if (!filtered.isEmpty()) {
                return filtered;
            }
            log.info("限定 categoryId {} 向量检索无命中，降级为全库无条件检索", keys);
            return vectorSimilaritySearch(question, null);
        } catch (Exception ex) {
            log.warn("categoryId 过滤向量检索失败，降级为全库无条件检索：{}", ex.toString());
            return vectorSimilaritySearch(question, null);
        }

    }

    /**
     * 向量相似度检索
     *
     * @param question 问题
     * @param filter   过滤器
     * @return 相关的文档列表
     */
    private List<Document> vectorSimilaritySearch(String question, Filter.Expression filter) {
        SearchRequest.Builder b = SearchRequest.builder()
                .query(question)
                .topK(RAG_TOP_K)
                .similarityThreshold(0.0);
        if (filter != null) {
            b.filterExpression(filter);
        }
        return vectorStore.similaritySearch(b.build());
    }

    /**
     * 构建类别 ID 过滤器
     *
     * @param categoryIdsAsString 类别 ID 字符串集合
     * @return 过滤器表达式
     */
    private static Filter.Expression buildCategoryIdFilter(Set<String> categoryIdsAsString) {
        FilterExpressionBuilder fb = new FilterExpressionBuilder();
        if (categoryIdsAsString.size() == 1) {
            return fb.eq("categoryId", categoryIdsAsString.iterator().next()).build();
        }
        List<Object> values = new ArrayList<>(categoryIdsAsString);
        return fb.in("categoryId", values).build();

    }

    /**
     * 构建 RAG 用户消息
     *
     * @param question 用户问题
     * @param cited    检索到的文档列表
     * @return RAG 用户消息
     */
    private static String buildRagUserMessage(String question, List<Document> cited) {
        if (cited == null || cited.isEmpty()) {
            return """
                    
                    （知识库检索未命中足够相关的片段，请直接依据系统说明作答。）
                    
                    用户问题：
                    
                    """ + question;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("以下是检索到的知识片段，请严格据此回答；片段相互冲突时优先采纳与问题最直接相关的表述。\n\n");
        int i = 1;
        for (Document d : cited) {
            Map<String, Object> meta = d.getMetadata();
            String title = meta.get("title") != null ? String.valueOf(meta.get("title")) : "(无标题)";
            sb.append("### 片段 ").append(i++).append(" · ").append(title).append("\n");
            String text = d.getText();
            if (text != null) {
                sb.append(text.strip()).append("\n\n");
            }
        }
        sb.append("---\n用户问题：\n").append(question.strip());
        return sb.toString();

    }

    /**
     * 将文档列表转换为引用列表
     *
     * @param docs 文档列表
     * @return 引用列表
     */
    private static List<Map<String, Object>> toRefs(List<Document> docs) {
        List<Map<String, Object>> refs = new ArrayList<>();
        for (Document d : docs) {
            Map<String, Object> m = new LinkedHashMap<>();
            Map<String, Object> meta = d.getMetadata();
            m.put("title", meta.get("title"));
            m.put("docId", meta.get("docId"));
            m.put("categoryId", meta.get("categoryId"));
            String tx = d.getText();
            if (tx != null && tx.length() > 240) {
                tx = tx.substring(0, 240) + "…";
            }
            m.put("snippet", tx);
            refs.add(m);
        }
        return refs;
    }

    /**
     * 列出会话消息
     *
     * @param chatMessageDTO 会话消息DTO
     * @return 会话消息列表
     */
    public ChatMessageDTO listMessages(ChatMessageDTO chatMessageDTO) {
        ChatSessionDTO chatSessionDTO = chatSessionService.queryById(ChatSessionDTO.builder().id(chatMessageDTO.getSessionId()).build());
        if (Objects.isNull(chatSessionDTO) || Objects.isNull(chatSessionDTO.getId()) || !Objects.equals(chatSessionDTO.getUserId(), chatMessageDTO.getUserId())) {
            log.error("会话不存在或者无权限查询此会话的会话消息 List!");
            throw new ServiceException("sessions.no.permissions", new String[]{"会话不存在或者无权限查询此会话的会话消息 List!"});
        }

        List<ChatMessage> chatMessageList = this.list(Wrappers.<ChatMessage>lambdaQuery()
                .eq(ChatMessage::getSessionId, chatMessageDTO.getSessionId())
                .orderByAsc(ChatMessage::getId));

        return ChatMessageDTO.builder()
                .dataList(chatMessageMapper.chatMessageListToChatMessageDTOList(chatMessageList))
                .page(1)
                .size(chatMessageList.size())
                .total(Long.valueOf(String.valueOf(chatMessageList.size())))
                .build();
    }

    /**
     * 根据会话 ID 删除会话消息
     *
     * @param chatMessageDTO 会话消息DTO {@link ChatMessageDTO}
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBySessionId(ChatMessageDTO chatMessageDTO) {
        this.remove(Wrappers.<ChatMessage>lambdaQuery().eq(ChatMessage::getSessionId, chatMessageDTO.getSessionId()));
    }
}
