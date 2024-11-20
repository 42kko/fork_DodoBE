package dododocs.dododocs.analyze.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ExternalAiZipAnalyzeResponse {

    @JsonProperty("readme_s3_key")
    private String readMeS3Key;

    @JsonProperty("docs_s3_key")
    private String docsS3Key;

    private ExternalAiZipAnalyzeResponse() {
    }

    public ExternalAiZipAnalyzeResponse(final String readMeS3Key, final String docsS3Key) {
        this.readMeS3Key = readMeS3Key;
        this.docsS3Key = docsS3Key;
    }
}
