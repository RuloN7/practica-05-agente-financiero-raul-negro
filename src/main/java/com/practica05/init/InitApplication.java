package com.practica05.init;

import com.practica05.init.tool.Tools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InitApplication {

	@Value("${system.text}")
	private String systemPrompt;

	public static void main(String[] args) {
		SpringApplication.run(InitApplication.class, args);
	}

	@Bean
    public ChatClient chatClient(ChatModel model, ChatMemory chatMemory, Tools tools) {
		return ChatClient.builder(model)
				.defaultSystem(systemPrompt)
				.defaultAdvisors(
						MessageChatMemoryAdvisor.builder(chatMemory)
								.build()
				)
				.defaultTools(tools)
				.build();
	}

	@Bean
	public ChatMemory chatMemory(JdbcChatMemoryRepository repository) {
		return MessageWindowChatMemory.builder()
				.chatMemoryRepository(repository)
				.maxMessages(20)
				.build();
	}

}
