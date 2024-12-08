package dododocs.dododocs.analyze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class DownloadAiAnalyzeResponse {
    private List<FileDetail> summaryFiles;
    private List<FileDetail> regularFiles;

    @Getter
    @AllArgsConstructor
    public static class FileDetail {
        private String fileName;
        private String fileContents;
    }

    public DownloadAiAnalyzeResponse(List<FileDetail> summaryFiles, List<FileDetail> regularFiles) {
        this.summaryFiles = summaryFiles;
        this.regularFiles = regularFiles;
    }
}
