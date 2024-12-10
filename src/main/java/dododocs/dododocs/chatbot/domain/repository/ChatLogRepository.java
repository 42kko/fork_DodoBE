package dododocs.dododocs.chatbot.domain.repository;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.chatbot.domain.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
    List<ChatLog> findTop3ByRepoAnalyzeOrderBySequenceDesc(final RepoAnalyze repoAnalyze);
    List<ChatLog> findByRepoAnalyze(final RepoAnalyze repoAnalyze, final Pageable pageable);
}
