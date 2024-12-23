package dododocs.dododocs.chatbot.presentation;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.analyze.exception.NoExistRepoAnalyzeException;
import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import dododocs.dododocs.chatbot.application.ChatbotService;
import dododocs.dododocs.chatbot.domain.ChatLog;
import dododocs.dododocs.chatbot.domain.repository.ChatLogRepository;
import dododocs.dododocs.chatbot.dto.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    private final ChatbotService chatbotService;
    private final ChatLogRepository chatLogRepository;
    private final RepoAnalyzeRepository repoAnalyzeRepository;
    private final WebClient webClient;

    @Value("${ai.basic_url}")
    private String aiBasicUrl;


    @PostMapping("/question/save/{registeredRepoId}")
    public ExternalQuestToChatbotResponse questionToChatbotAndSaveLogs(@PathVariable final Long registeredRepoId,
                                                                       @RequestBody final QuestToChatbotRequest questToChatbotRequest) {
        return chatbotService.questionToChatbotAndSaveLogs(registeredRepoId, questToChatbotRequest.getQuestion());
    }

    @GetMapping("/logs/{registeredRepoId}")
    public ResponseEntity<FindChatLogResponses> findChatbotHistory(@Authentication final Accessor accessor,
                                                                   @PathVariable final Long registeredRepoId,
                                                                   @PageableDefault(size = 30) final Pageable pageable) {
        return ResponseEntity.ok(chatbotService.findChatbotHistory(registeredRepoId, pageable));
    }

    /* @GetMapping(value = "/stream-from-backend", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TestWebFluxResponse> streamFromBackend() {
        System.out.println("AI 서버로 데이터 요청 시작...");
        System.out.println(aiBasicUrl);
        return webClient.get()
                .uri("/chat")
                .retrieve()
                .bodyToFlux(String.class) // SSE 데이터를 Flux로 수신
                .map(data -> {
                    System.out.println("AI 서버에서 수신한 데이터: " + data);
                    return new TestWebFluxResponse(data);
                })
                .doOnError(error -> System.err.println("AI 서버 연결 에러: " + error.getMessage()))
                .onErrorResume(error -> Flux.just(new TestWebFluxResponse("AI 서버와 연결 중 문제가 발생했습니다.")));
    } */

    @GetMapping(value = "/stream-and-save-test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TestWebFluxResponse> streamAndSaveChatLogsTest() {
        final RepoAnalyze repoAnalyze = repoAnalyzeRepository.findById(1L)
                .orElseThrow(() -> new NoExistRepoAnalyzeException("레포지토리 정보가 존재하지 않습니다."));

        StringBuilder aggregatedText = new StringBuilder();

        return webClient.get()
                .uri("/chat22222222")
                .retrieve()
                .bodyToFlux(String.class)
                .map(data -> {
                    System.out.println("AI 서버에서 수신한 데이터: " + data);
                    aggregatedText.append(data).append(" ");
                    return new TestWebFluxResponse(data);
                })
                .doOnComplete(() -> {
                    String aggregatedResult = aggregatedText.toString().trim();
                    chatLogRepository.save(new ChatLog("Aggregated Question", aggregatedResult, repoAnalyze));
                    System.out.println("전체 텍스트 저장 완료: " + aggregatedResult);
                })
                .doOnError(error -> System.err.println("AI 서버 연결 에러: " + error.getMessage()))
                .onErrorResume(error -> Flux.just(new TestWebFluxResponse("AI 서버와 연결 중 문제가 발생했습니다.")));
    }

    @PostMapping(value = "/stream-and-save/{registeredRepoId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TestWebFluxResponse> streamAndSaveChatLogs(@PathVariable final Long registeredRepoId,
                                                           @RequestBody final QuestToChatbotRequest questToChatbotRequest) {
        final RepoAnalyze repoAnalyze = repoAnalyzeRepository.findById(registeredRepoId)
                .orElseThrow(() -> new NoExistRepoAnalyzeException("레포지토리 정보가 존재하지 않습니다."));

        final List<ChatLog> chatLogs = chatLogRepository.findTop3ByRepoAnalyzeOrderBySequenceDesc(repoAnalyze);
        final List<ExternalQuestToChatbotRequest.RecentChatLog> recentChatLogs = chatLogs.stream()
                .map(chatLog -> new ExternalQuestToChatbotRequest.RecentChatLog(chatLog.getQuestion(), chatLog.getAnswer()))
                .toList();

        System.out.println("레포 URL: " + repoAnalyze.getRepoUrl());

        final ExternalQuestToChatbotRequest externalQuestToChatbotRequest = new ExternalQuestToChatbotRequest(
                repoAnalyze.getRepoUrl(),
                questToChatbotRequest.getQuestion(),
                recentChatLogs,
                true
        );

        StringBuilder aggregatedText = new StringBuilder();

        return webClient.post()
                .uri("/chat")
                .bodyValue(externalQuestToChatbotRequest)
                .retrieve()
                .bodyToFlux(String.class)
                .map(data -> {
                    System.out.println("AI 서버에서 수신한 데이터: " + data);
                    aggregatedText.append(data).append(" ");
                    return new TestWebFluxResponse(data);
                })
                .doOnComplete(() -> {
                    String aggregatedResult = aggregatedText.toString().trim();
                    chatLogRepository.save(new ChatLog(questToChatbotRequest.getQuestion(), aggregatedResult, repoAnalyze));
                    System.out.println("전체 텍스트 저장 완료: " + aggregatedResult);
                })
                .doOnError(error -> System.out.println("AI 서버 연결 에러: " + error.getMessage()))
                .onErrorResume(error -> {
                    System.out.println("AI 서버와 연결 중 문제가 발생했습니다.");
                    return Flux.just(new TestWebFluxResponse("AI 서버와 연결 중 문제가 발생했습니다."));
                });
    }
}
