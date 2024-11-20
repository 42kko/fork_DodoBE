package dododocs.dododocs.analyze.dto;

import lombok.Getter;

@Getter
public class DownloadAiAnalyzeRequest {
    private String repoName;

    private DownloadAiAnalyzeRequest() {
    }

    public DownloadAiAnalyzeRequest(final String repoName) {
        this.repoName = repoName;
    }
}
