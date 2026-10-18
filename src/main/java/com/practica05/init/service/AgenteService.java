package com.practica05.init.service;

import com.practica05.init.tool.Tools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class AgenteService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public String chat(String mensaje, String conversationId) {
        AtomicBoolean operacionRegistrada = new AtomicBoolean(false);
        String respuesta = chatClient.prompt()
                .user(mensaje)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .toolContext(Map.of(Tools.OPERACION_REGISTRADA, operacionRegistrada))
                .call()
                .content();
        if (operacionRegistrada.get()) {
            chatMemory.clear(conversationId);
        }
        return respuesta;
    }

}
