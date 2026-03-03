package com.nulhart.openai;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service

@Data
public class OpenAIClient {
    private final ChatClient chatClient;

    public OpenAIClient(ChatClient.Builder chatClientBuilder) {
        this.chatClient= chatClientBuilder.build();
    }
}
