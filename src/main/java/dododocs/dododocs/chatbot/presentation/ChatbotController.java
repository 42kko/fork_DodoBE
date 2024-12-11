package dododocs.dododocs.chatbot.presentation;

import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import dododocs.dododocs.chatbot.application.ChatbotService;
import dododocs.dododocs.chatbot.dto.ExternalQuestToChatbotResponse;
import dododocs.dododocs.chatbot.dto.FindChatLogResponses;
import dododocs.dododocs.chatbot.dto.QuestToChatbotRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    private final ChatbotService chatbotService;

    @PostMapping("/question/save/{registeredRepoId}")
    public ResponseEntity<ExternalQuestToChatbotResponse> questionToChatbotAndSaveLogs(@Authentication final Accessor accessor,
                                                             @PathVariable final Long registeredRepoId,
                                                             @RequestBody final QuestToChatbotRequest questToChatbotRequest) {
        return ResponseEntity.ok(chatbotService.questionToChatbotAndSaveLogs(registeredRepoId, questToChatbotRequest.getQuestion()));
    }

    @GetMapping("/logs/{registeredRepoId}")
    public ResponseEntity<FindChatLogResponses> findChatbotHistory(@Authentication final Accessor accessor,
                                                                   @PathVariable final Long registeredRepoId,
                                                                   @PageableDefault(size = 30) final Pageable pageable) {
        return ResponseEntity.ok(chatbotService.findChatbotHistory(registeredRepoId, pageable));
    }
}
