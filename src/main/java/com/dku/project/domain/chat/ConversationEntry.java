package com.dku.project.domain.chat;


/**
 * 스레드 히스토리의 항목을 역할(role)과 내용(content)으로 감싼 DTO입니다.
 */
public record ConversationEntry(Role role, String content) {

    public enum Role {
        ASSISTANT,
        USER
    }
}
