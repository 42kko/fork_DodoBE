package dododocs.dododocs.chatbot.infrastructure;

import dododocs.dododocs.chatbot.dto.ExternalQuestToChatbotRequest;
import dododocs.dododocs.chatbot.dto.ExternalQuestToChatbotResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExternalChatbotClient {
    private final String aiBasicUrl;
    private static final String CHATBOT_QUESTION_REQUEST_URL_PREFIX = "/chat";
    private final RestTemplate restTemplate;

    public ExternalChatbotClient(final RestTemplate restTemplate,
                                 @Value("${ai.basic_url}") final String aiBasicUrl) {
        this.restTemplate = restTemplate;
        this.aiBasicUrl = aiBasicUrl;
    }

    public ExternalQuestToChatbotResponse questToChatbot(final ExternalQuestToChatbotRequest questToChatbotRequest) {
        return requestQuestion(questToChatbotRequest);
    }

    private ExternalQuestToChatbotResponse requestQuestion(final ExternalQuestToChatbotRequest questToChatbotRequest) {
        final Map<String, String> urlVariables = new HashMap<>();

        final ResponseEntity<ExternalQuestToChatbotResponse> responseEntity = restTemplate.exchange(
                aiBasicUrl + CHATBOT_QUESTION_REQUEST_URL_PREFIX,
                HttpMethod.POST,
                new HttpEntity<>(questToChatbotRequest),
                ExternalQuestToChatbotResponse.class,
                urlVariables
        );

        return responseEntity.getBody();
    }
}

/*
@Component
public class ExternalAiRecommendModelClient implements ExternalRecommendModelClient {
    private static final String RECOMMEND_TRIP_LIST_REQUEST_URL = "http://ai:8000/travel/custom/model?page={page}";
    private final RestTemplate restTemplate;

    public ExternalAiRecommendModelClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public RecommendTripsByVisitedLogsResponse recommendTripsByVisitedLogs(final RecommendTripsByVisitedLogsRequest request) {
        return requestRecommendTrips(request);
    }

    private RecommendTripsByVisitedLogsResponse requestRecommendTrips(final RecommendTripsByVisitedLogsRequest request) {
        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("page", String.valueOf(request.getPage()));

        final ResponseEntity<RecommendTripsByVisitedLogsResponse> responseEntity = fetchRecommendTripsByVisitedLogs(request, uriVariables);
        return responseEntity.getBody();
    }

    private ResponseEntity<RecommendTripsByVisitedLogsResponse> fetchRecommendTripsByVisitedLogs(final RecommendTripsByVisitedLogsRequest request, final Map<String, String> uriVariables) {
        try {
            return restTemplate.exchange(
                    RECOMMEND_TRIP_LIST_REQUEST_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(new PreferredLocationRequest(request.getPreferredLocation())),
                    RecommendTripsByVisitedLogsResponse.class,
                    uriVariables
            );
        } catch (final ResourceAccessException | HttpClientErrorException e) {
            throw new InvalidAIServerException("AI 서버에 접근할 수 없는 상태입니다.");
        }
        catch (final RestClientException e) {
            throw new InvalidAIServerException("AI 서버에 예기치 못한 오류가 발생했습니다.");
        }
    }
}
 */