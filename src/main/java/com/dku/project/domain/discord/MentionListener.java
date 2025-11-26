package com.dku.project.domain.discord;

import com.dku.project.domain.chat.RulebookChatService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 멘션 기반 회칙 Q&A 리스너.
 * - 스레드 내부 멘션: 스레드 전체 맥락을 모아 답변 후 스레드에 reply
 * - 일반 채널 멘션: 멘션 메시지를 기점으로 스레드 생성 후 답변
 */
@Component
@RequiredArgsConstructor
public class MentionListener extends ListenerAdapter {

    private static final int THREAD_HISTORY_LIMIT = 30;

    private final JDA jda;
    private final RulebookChatService rulebookChatService;

    @PostConstruct
    void register() {
        jda.addEventListener(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // 사람에 의해 호출되었는지 확인
        if (!isMentionedByUser(event)) {
            return;
        }

        if (event.isFromThread()) {
            handleThreadMention(event);
        } else {
            handleChannelMention(event);
        }
    }

    private boolean isMentionedByUser(MessageReceivedEvent event) {
        // 실제로 봇을 멘션한 사용자 메시지인지 확인한다.
        return !event.getAuthor().isBot()
                && !event.isWebhookMessage()
                && event.getMessage().getMentions().isMentioned(event.getJDA().getSelfUser());
    }

    private void handleThreadMention(MessageReceivedEvent event) {
        ThreadChannel thread = event.getChannel().asThreadChannel();

        String context = buildThreadContext(thread);
        String answer = rulebookChatService.answer(event.getMessage().getContentDisplay(), context);

        thread.sendMessage(answer).queue();
    }

    private void handleChannelMention(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String baseQuestion = message.getContentDisplay();
        String threadName = "회칙-문의-" + DateTimeFormatter.ofPattern("HHmmss").format(message.getTimeCreated());

        message.createThreadChannel(threadName).queue(thread -> {
            String answer = rulebookChatService.answer(baseQuestion, "");
            thread.sendMessage(answer).queue();
        });
    }

    private String buildThreadContext(ThreadChannel thread) {
        List<Message> messages = thread.getHistory().retrievePast(THREAD_HISTORY_LIMIT).complete();

        return messages.stream()
                .sorted(Comparator.comparing(Message::getTimeCreated))
                .map(m -> "%s: %s".formatted(m.getAuthor().getName(), m.getContentDisplay()))
                .collect(Collectors.joining("\n"));
    }
}
