package dododocs.dododocs.analyze.presentation;

import dododocs.dododocs.analyze.application.RepoRegisterService;
import dododocs.dododocs.analyze.dto.DeleteRepoRegisterRequest;
import dododocs.dododocs.analyze.dto.UpdateChatbotDocsRepoAnalyzeReadyStatusRequest;
import dododocs.dododocs.analyze.dto.UpdateReadmeDocsRepoAnalyzeReadyStatusRequest;
import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RepoRegisterController {
    private final RepoRegisterService repoRegisterService;

    @DeleteMapping
    public ResponseEntity<Void> deleteRegisteredRepos(@Authentication final Accessor accessor,
                                                final @RequestBody DeleteRepoRegisterRequest deleteRepoRegisterRequest) {
        repoRegisterService.removeRegisteredRepos(deleteRepoRegisterRequest.getRegisteredRepoId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/status/readme/docs")
    public void updateRepoAnalyzeReadmeDocsReadyStatus(final @RequestBody UpdateReadmeDocsRepoAnalyzeReadyStatusRequest updateRepoAnalyzeReadyStatusRequest) {
        repoRegisterService.updateReadmeDocsRepoAnalyzeReadyStatus(updateRepoAnalyzeReadyStatusRequest);
    }

    @PutMapping("/status/chatbot")
    public void updateRepoAnalyzeChatbotReadyStatus(final @RequestBody UpdateChatbotDocsRepoAnalyzeReadyStatusRequest updateChatbotDocsRepoAnalyzeReadyStatusRequest) {
        repoRegisterService.updateChatbotRepoAnalyzeReadyStatus(updateChatbotDocsRepoAnalyzeReadyStatusRequest);
    }
}
