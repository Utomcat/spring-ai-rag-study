package com.ranyk.spring.ai.rag.knowledge.database.config;

import com.ranyk.spring.ai.rag.knowledge.database.ai.advisor.CustomSimpleLoggerAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS_NAME: AdvisorConfiguration.java
 *
 * @author ranyk
 * @version V1.0
 * @description: ChatClient监管切面 Advisor 配置类
 * @date: 2026-06-25
 */
@Slf4j
@Configuration
public class AdvisorConfiguration {

    /**
     * 创建一个 Spring AI 自带的 SimpleLoggerAdvisor Bean 对象
     *
     * @return 返回一个{@link SimpleLoggerAdvisor} 对象
     */
    @Bean
    public SimpleLoggerAdvisor simpleLoggerAdvisor() {
        return new SimpleLoggerAdvisor();
    }

    /**
     * 创建一个 自定义的 CustomSimpleLoggerAdvisor Bean 对象
     *
     * @return 返回一个{@link CustomSimpleLoggerAdvisor} 对象
     */
    @Bean
    public CustomSimpleLoggerAdvisor customSimpleLoggerAdvisor() {
        return new CustomSimpleLoggerAdvisor();
    }

}
