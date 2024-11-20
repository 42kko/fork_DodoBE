package dododocs.dododocs.analyze.dto;

import lombok.Getter;

@Getter
public class DownloadAiAnalyzeRequest {
    private String repositoryName;

    private DownloadAiAnalyzeRequest() {
    }

    public DownloadAiAnalyzeRequest(final String repositoryName) {
        this.repositoryName = repositoryName;
    }
}
