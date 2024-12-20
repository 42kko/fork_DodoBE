package dododocs.dododocs.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dododocs.dododocs.chatbot.domain.ChatLog;
import lombok.Getter;

import java.util.List;

@Getter
public class ExternalQuestToChatbotRequest {

    @JsonProperty("repo_url")
    private String repoUrl;

    @JsonProperty("query")
    private String query;

    @JsonProperty("chat_history")
    private List<RecentChatLog> chatHistory;

    @Getter
    public static class RecentChatLog {
        private String question;
        private String answer;

        private RecentChatLog() {
        }

        public RecentChatLog(final String question, final String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    private ExternalQuestToChatbotRequest() {
    }

    public ExternalQuestToChatbotRequest(final String repoUrl, final String query, final List<RecentChatLog> chatHistory) {
        this.repoUrl = repoUrl;
        this.query = query;
        this.chatHistory = chatHistory;
    }
}
