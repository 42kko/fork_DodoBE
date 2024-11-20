package dododocs.dododocs.analyze.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class DownloadAiAnalyzeResponse {
    private List<Map<String, String>> summaryFiles;
    private List<Map<String, String>> regularFiles;

    private DownloadAiAnalyzeResponse() {
    }

    public DownloadAiAnalyzeResponse(List<Map<String, String>> summaryFiles, List<Map<String, String>> regularFiles) {
        this.summaryFiles = summaryFiles;
        this.regularFiles = regularFiles;
    }
}