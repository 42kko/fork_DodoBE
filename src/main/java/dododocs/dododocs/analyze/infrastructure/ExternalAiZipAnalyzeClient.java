package dododocs.dododocs.analyze.infrastructure;

import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeRequest;
import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeResponse;
import dododocs.dododocs.auth.exception.NoExistMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExternalAiZipAnalyzeClient {
    private final String aiBasicUrl;
    private final String AI_ZIP_DOWNLOAD_AND_ANALYZE_REQUEST_URL_PREFIX = "/generate";
    private final RestTemplate restTemplate;

    public ExternalAiZipAnalyzeClient(final RestTemplate restTemplate,
                                      @Value("${ai.basic_url}") final String aiBasicUrl) {
        this.restTemplate = restTemplate;
        this.aiBasicUrl = aiBasicUrl;
    }

    public ExternalAiZipAnalyzeResponse requestAiZipDownloadAndAnalyze(final ExternalAiZipAnalyzeRequest request) {
        return requestAnalyze(request);
    }

    private ExternalAiZipAnalyzeResponse requestAnalyze(final ExternalAiZipAnalyzeRequest request) {
        final Map<String, String> uriVariables = new HashMap<>();

        final ResponseEntity<ExternalAiZipAnalyzeResponse> responseEntity = restTemplate.exchange(
                aiBasicUrl + AI_ZIP_DOWNLOAD_AND_ANALYZE_REQUEST_URL_PREFIX,
                HttpMethod.POST,
                new HttpEntity<>(request),
                ExternalAiZipAnalyzeResponse.class,
                uriVariables
        );

        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        throw new NoExistMemberException();
    }
}
