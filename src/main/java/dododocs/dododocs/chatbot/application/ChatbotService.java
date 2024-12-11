package dododocs.dododocs.chatbot.application;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.analyze.exception.NoExistRepoAnalyzeException;
import dododocs.dododocs.chatbot.domain.ChatLog;
import dododocs.dododocs.chatbot.domain.repository.ChatLogRepository;
import dododocs.dododocs.chatbot.dto.ExternalQuestToChatbotRequest;
import dododocs.dododocs.chatbot.dto.ExternalQuestToChatbotResponse;
import dododocs.dododocs.chatbot.dto.FindChatLogResponses;
import dododocs.dododocs.chatbot.infrastructure.ExternalChatbotClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatbotService {
    private final ExternalChatbotClient externalChatbotClient;
    private final RepoAnalyzeRepository repoAnalyzeRepository;
    private final ChatLogRepository chatLogRepository;

    public ExternalQuestToChatbotResponse questionToChatbotAndSaveLogs(final long registeredRepoId, final String question) {
        final RepoAnalyze repoAnalyze = repoAnalyzeRepository.findById(registeredRepoId)
                .orElseThrow(() -> new NoExistRepoAnalyzeException("레포지토리 정보가 존재하지 않습니다."));

        final List<ChatLog> chatLogs = chatLogRepository.findTop3ByRepoAnalyzeOrderBySequenceDesc(repoAnalyze);

        final List<ExternalQuestToChatbotRequest.RecentChatLog> recentChatLogs = chatLogs.stream()
                .map(chatLog -> new ExternalQuestToChatbotRequest.RecentChatLog(chatLog.getQuestion(), chatLog.getAnswer()))
                .toList();

        final ExternalQuestToChatbotRequest questToChatbotRequest = new ExternalQuestToChatbotRequest(
                repoAnalyze.getRepoUrl(),
                question,
                recentChatLogs,
                false
        );
        return externalChatbotClient.questToChatbot(questToChatbotRequest);
    }

    public FindChatLogResponses findChatbotHistory(final long registeredRepoId, final Pageable pageable) {
        final RepoAnalyze repoAnalyze = repoAnalyzeRepository.findById(registeredRepoId)
                .orElseThrow(() -> new NoExistRepoAnalyzeException("레포 정보 없습니다."));

        return new FindChatLogResponses(chatLogRepository.findByRepoAnalyzeOrderBySequenceWithPagination(repoAnalyze, pageable).getContent());
    }
}

