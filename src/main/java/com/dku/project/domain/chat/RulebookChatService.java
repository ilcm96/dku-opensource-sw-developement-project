package com.dku.project.domain.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RulebookChatService {

    private final ChatClient chatClient;
    private final OpenAiChatOptions nonStreamChatOptions;

    /**
     * @param question 사용자가 멘션과 함께 보낸 질문 텍스트
     * @param history  스레드의 이전 대화 기록(없으면 빈 리스트)
     * @return LLM이 생성한 한국어 답변
     */
    public String answer(String question, List<ConversationEntry> history) {
        List<Message> messages = RulebookPrompts.buildMessages(
                question == null ? "" : question,
                history == null ? List.of() : history
        );

        return chatClient
                .prompt()
                .messages(messages)
                .options(nonStreamChatOptions)
                .call()
                .content();
    }
}
