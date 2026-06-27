package com.ranyk.spring.ai.rag.knowledge.database;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 项目测试类
 */
@Slf4j
@SpringBootTest
class SpringAiRagKnowledgeDatabaseApplicationTests {

    @Test
    void contextLoads() {
    }

    /**
     * 测试 - 使用 Hutool 工具类生成 BCrypt 密码
     */
    @Test
    @DisplayName("测试 - 使用 Hutool 工具类生成 BCrypt 密码")
    void testHutoolGenerateBCrypt() {
        String password = "12345678";
        String bcryptPassword = DigestUtil.bcrypt(password);
        // 输出 BCrypt 密码: $2a$10$mz4rExfImEzO4yoxMaTAreywcjF8xxpl0bZVILGGgVKWZVVPwGaf2
        log.info("BCrypt 密码: {}", bcryptPassword);
    }

}
