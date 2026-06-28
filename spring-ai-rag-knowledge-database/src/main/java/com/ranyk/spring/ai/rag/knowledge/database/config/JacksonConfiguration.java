package com.ranyk.spring.ai.rag.knowledge.database.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ranyk.spring.ai.rag.knowledge.database.service.chat.message.ChatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * CLASS_NAME: JacksonConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 提供全局 {@link ObjectMapper}，供业务类（如 {@link ChatMessageService}）注入
 * @date: 2026-06-28
 */
@Slf4j
@Configuration
public class JacksonConfiguration {

    /**
     * 提供全局 {@link ObjectMapper}，供业务类（如 {@link ChatMessageService}）注入, 与 Spring 生态兼容的 JSON 处理器（含 Java 8 时间等模块自动发现）
     *
     * @return 全局 {@link ObjectMapper} 实例
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }
}
