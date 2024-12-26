package dododocs.dododocs.chatbot.domain.repository;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.chatbot.domain.ChatLog;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
    List<ChatLog> findTop3ByRepoAnalyzeOrderBySequenceDesc(final RepoAnalyze repoAnalyze);

    @Query("SELECT c FROM ChatLog c WHERE c.repoAnalyze = :repoAnalyze ORDER BY c.sequence DESC ")
    Page<ChatLog> findByRepoAnalyzeOrderBySequenceWithPagination(@Param("repoAnalyze") final RepoAnalyze repoAnalyze, final Pageable pageable);
}
