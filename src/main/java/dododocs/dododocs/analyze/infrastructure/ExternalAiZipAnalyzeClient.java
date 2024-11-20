package dododocs.dododocs.analyze.infrastructure;

import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeRequest;
import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeResponse;
import dododocs.dododocs.auth.exception.NoExistMemberException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExternalAiZipAnalyzeClient {
    private static final String AI_ZIP_DOWNLOAD_AND_ANALYZE_REQUEST_URL = "http://localhost:8000/travel/custom/model?page={page}";
    private final RestTemplate restTemplate;

    public ExternalAiZipAnalyzeClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ExternalAiZipAnalyzeResponse requestAiZipDownloadAndAnalyze(final ExternalAiZipAnalyzeRequest request) {
        return requestAnalyze(request);
    }

    private ExternalAiZipAnalyzeResponse requestAnalyze(final ExternalAiZipAnalyzeRequest request) {
        final Map<String, String> uriVariables = new HashMap<>();

        final ResponseEntity<ExternalAiZipAnalyzeResponse> responseEntity = restTemplate.exchange(
                AI_ZIP_DOWNLOAD_AND_ANALYZE_REQUEST_URL,
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
