package dododocs.dododocs.analyze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateChatbotDocsRepoAnalyzeReadyStatusRequest {
    private String repoUrl;
    private boolean chatbotCompleted;
}
