package com.ranyk.spring.ai.rag.knowledge.database;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 */
@MapperScan(value = {"com.ranyk.spring.ai.rag.knowledge.database.repository"})
@SpringBootApplication(scanBasePackages = {"com.ranyk.spring.ai.rag.knowledge.database"})
public class SpringAiRagKnowledgeDatabaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiRagKnowledgeDatabaseApplication.class, args);
    }

}
