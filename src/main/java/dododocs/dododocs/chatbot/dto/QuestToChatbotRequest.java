package dododocs.dododocs.chatbot.dto;

import lombok.Getter;

@Getter
public class QuestToChatbotRequest {
    private String question;

    private QuestToChatbotRequest() {
    }

    public QuestToChatbotRequest(final String question) {
        this.question = question;
    }
}
