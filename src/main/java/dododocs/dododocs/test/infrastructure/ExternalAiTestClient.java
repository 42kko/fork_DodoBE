package dododocs.dododocs.test.infrastructure;

import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeRequest;
import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeResponse;
import dododocs.dododocs.auth.exception.NoExistMemberException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExternalAiTestClient {
    private final String aiBasicUrl;
    private final String AI_PING_TEST_URL_PREFIX = "/ping";
    private final RestTemplate restTemplate;

    public ExternalAiTestClient(final RestTemplate restTemplate,
                                @Value("${ai.basic_url}") final String aiBasicUrl) {
        this.aiBasicUrl = aiBasicUrl;
        this.restTemplate = restTemplate;
    }

    public String requestTestAI() {
        return requestAnalyze();
    }

    private String requestAnalyze() {
        final Map<String, String> uriVariables = new HashMap<>();

        // Use an empty HttpEntity with no headers or body
        final HttpEntity<Void> httpEntity = new HttpEntity<>(null);

        final ResponseEntity<Void> responseEntity = restTemplate.exchange(
                aiBasicUrl + AI_PING_TEST_URL_PREFIX,
                HttpMethod.GET,
                httpEntity,
                Void.class,
                uriVariables
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return "AI TEST SUCCESSFUL~~~~~~~~!!";
        }
        return "AI TEST FAILED~~~~~~~!!";
    }
}
