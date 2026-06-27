package com.ranyk.spring.ai.rag.knowledge.database.config;

import com.ranyk.spring.ai.rag.knowledge.database.ai.advisor.CustomSimpleLoggerAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS_NAME: OpenAiConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: ChatClient 配置类
 * @date: 2026-06-22
 */
@Slf4j
@Configuration
public class ChatClientConfiguration {

    /**
     * 创建一个 ChatClient Bean 对象
     *
     * @param chatModel                 ChatModel 对象, 如果未手动进行配置, 则使用的是 Spring AI 自动配置创建的 {@link ChatModel} 对象, 根据配置文件中配置的 ChatModel
     * @param customSimpleLoggerAdvisor 自定义的简单日志记录顾问对象, {@link CustomSimpleLoggerAdvisor}
     * @param simpleLoggerAdvisor       Spring AI 自带的简单日志记录顾问对象, {@link SimpleLoggerAdvisor}
     * @return 返回一个创建好的 {@link ChatClient} 对象
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel, CustomSimpleLoggerAdvisor customSimpleLoggerAdvisor, SimpleLoggerAdvisor simpleLoggerAdvisor) {
        return ChatClient
                // 设置 ChatClient 对象的 ChatModel
                .builder(chatModel)
                // 设置 ChatClient 对象的默认 Advisor 对象, 可设置多个
                .defaultAdvisors(
                        // 自定义简单日志记录顾问
                        customSimpleLoggerAdvisor,
                        // Spring AI 自带简单日志记录顾问
                        simpleLoggerAdvisor
                )
                // 构建 ChatClient 对象
                .build();
    }


}
