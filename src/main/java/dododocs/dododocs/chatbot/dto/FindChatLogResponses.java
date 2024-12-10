package dododocs.dododocs.chatbot.dto;

import dododocs.dododocs.chatbot.domain.ChatLog;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FindChatLogResponses {
    private List<FindChatLogResponse> findChatLogResponses;

    private FindChatLogResponses() {
    }

    public FindChatLogResponses(final List<ChatLog> chatLogs) {
        findChatLogResponses = toResponses(chatLogs);
    }

    private List<FindChatLogResponse> toResponses(final List<ChatLog> chatLogs) {
        return chatLogs.stream()
                .map(FindChatLogResponse::new)
                .collect(Collectors.toList());
    }
}
