package dododocs.dododocs.analyze.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ExternalAiZipAnalyzeRequest {
    // s3 key 값, 레포 주소 필요
    @JsonProperty("s3_path")
    private String s3Key;

    @JsonProperty("repo_url")
    private String repositoryUrl;

    @JsonProperty("include_test")
    private boolean includeTest;

    private ExternalAiZipAnalyzeRequest() {
    }

    public ExternalAiZipAnalyzeRequest(final String s3Key, final String repositoryUrl, final boolean includeTest) {
        this.s3Key = s3Key;
        this.repositoryUrl = repositoryUrl;
        this.includeTest = includeTest;
    }
}
