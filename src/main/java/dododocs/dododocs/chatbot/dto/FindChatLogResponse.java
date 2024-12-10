package dododocs.dododocs.chatbot.dto;

import dododocs.dododocs.chatbot.domain.ChatLog;
import lombok.Getter;

public class FindChatLogResponse {
    private String question;
    private String answer;

    private FindChatLogResponse() {
    }

    public FindChatLogResponse(final ChatLog chatLog) {
        this.question = chatLog.getQuestion();
        this.answer = chatLog.getAnswer();
    }
}
