package dododocs.dododocs.analyze.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

// repo_url, include_test, docs_key, readme_key, korean

// docs_key : msung99_moheng_main_DOCS.zip
// readme_key : msung99_moheng_main_README.md

@Getter
public class ExternalAiZipAnalyzeRequest {
    // s3 key 값, 레포 주소 필요
    @JsonProperty("s3_path")
    private String s3Key;

    @JsonProperty("repo_url")
    private String repositoryUrl;

    @JsonProperty("include_test")
    private boolean includeTest;

    @JsonProperty("blocks")
    private List<String> blocks;

    @JsonProperty("korean")
    private boolean korean;

    private ExternalAiZipAnalyzeRequest() {
    }

    public ExternalAiZipAnalyzeRequest(final String s3Key, final String repositoryUrl, final List<String> blocks, final boolean includeTest, final boolean korean) {
        this.s3Key = s3Key;
        this.blocks = blocks;
        this.repositoryUrl = repositoryUrl;

        this.includeTest = includeTest;
        this.korean = korean;
    }
}
