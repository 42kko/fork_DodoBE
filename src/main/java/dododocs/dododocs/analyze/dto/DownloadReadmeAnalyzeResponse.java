package dododocs.dododocs.analyze.dto;

import lombok.Getter;

@Getter
public class DownloadReadmeAnalyzeResponse {
    private String contents;

    private DownloadReadmeAnalyzeResponse() {
    }

    public DownloadReadmeAnalyzeResponse(final String contents) {
        this.contents = contents;
    }
}
