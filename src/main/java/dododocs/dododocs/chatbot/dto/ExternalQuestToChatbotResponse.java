package dododocs.dododocs.chatbot.dto;

import lombok.Getter;

@Getter
public class ExternalQuestToChatbotResponse {
    private String answer;

    private ExternalQuestToChatbotResponse() {
    }

    public ExternalQuestToChatbotResponse(final String answer) {
        this.answer = answer;
    }
}
