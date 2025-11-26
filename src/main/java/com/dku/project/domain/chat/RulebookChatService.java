package com.dku.project.domain.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RulebookChatService {

    private final ChatClient chatClient;
    private final OpenAiChatOptions nonStreamChatOptions;

    /**
     * @param question 사용자가 멘션과 함께 보낸 질문 텍스트
     * @param context  스레드의 이전 대화나 메시지 내용(없으면 빈 문자열)
     * @return LLM이 생성한 한국어 답변
     */
    public String answer(String question, String context) {
        String userContent = RulebookPrompts.userPrompt(question, context);

        var systemMessage = new SystemMessage(RulebookPrompts.SYSTEM_PROMPT);
        var userMessage = new UserMessage(userContent);

        return chatClient
                .prompt()
                .messages(systemMessage, userMessage)
                .options(nonStreamChatOptions)
                .call()
                .content();
    }
}
