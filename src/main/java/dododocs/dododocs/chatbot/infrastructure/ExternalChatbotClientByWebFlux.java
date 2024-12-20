package dododocs.dododocs.chatbot.infrastructure;

import dododocs.dododocs.analyze.exception.NoExistGitRepoException;
import dododocs.dododocs.analyze.exception.NoExistRepoAnalyzeException;
import dododocs.dododocs.chatbot.dto.ExternalQuestToChatbotRequest;
import dododocs.dododocs.chatbot.dto.ExternalQuestToChatbotResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ExternalChatbotClientByWebFlux {
    private final WebClient webClient;
    private final String aiBasicUrl;
    private static final String CHATBOT_QUESTION_REQUEST_URL_PREFIX = "/chat";

    public ExternalChatbotClientByWebFlux(@Value("${ai.basic_url}") final String aiBasicUrl, WebClient.Builder webClientBuilder) {
        this.aiBasicUrl = aiBasicUrl;
        this.webClient = WebClient.builder()
                .baseUrl(aiBasicUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE) // SSE 헤더
                .build();
    }

    public Mono<ExternalQuestToChatbotResponse> questToChatbot(final ExternalQuestToChatbotRequest questToChatbotRequest) {
        System.out.println("==========qweqweqwe");
        return webClient.method(HttpMethod.GET)
                .uri(aiBasicUrl + CHATBOT_QUESTION_REQUEST_URL_PREFIX)
                .bodyValue(questToChatbotRequest)
                .retrieve()
                .bodyToMono(ExternalQuestToChatbotResponse.class)
                .onErrorMap(throwable -> {
                    if (throwable instanceof RuntimeException) {
                        System.out.println(throwable.getMessage());
                        return new NoExistRepoAnalyzeException("레포지토리 결과물을 아직 생성중입니다. 잠시만 기다려주세요.");
                    }
                    return new NoExistGitRepoException("레포지토리 결과물을 아직 생성중입니다. 잠시만 기다려주세요.");
                });
    }
}
