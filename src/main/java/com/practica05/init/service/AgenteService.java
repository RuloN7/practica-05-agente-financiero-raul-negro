package com.practica05.init.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgenteService {

    private final ChatClient chatClient;

    public String chat(String mensaje, String conversationId) {
        return chatClient.prompt()
                .user(mensaje)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

}
