package com.ranyk.spring.ai.rag.knowledge.database.service.chat.session;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ranyk.spring.ai.rag.knowledge.database.domain.chat.session.entity.ChatSession;
import com.ranyk.spring.ai.rag.knowledge.database.repository.chat.session.ChatSessionRepository;
import org.springframework.stereotype.Service;

/**
 * CLASS_NAME: ChatSessionService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 聊天会话业务逻辑处理类
 * @date: 2026-06-27
 */
@Service
public class ChatSessionService extends ServiceImpl<ChatSessionRepository, ChatSession> {
}
