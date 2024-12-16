package dododocs.dododocs.chatbot.domain;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "chat_log")
@Entity
public class ChatLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "question")
    private String question;

    @Column(name = "answer", length = 8000)
    private String answer;

    @Column(name = "sequence")
    private Long sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_analyze_id", nullable = false)
    private RepoAnalyze repoAnalyze;

    protected ChatLog() {
    }

    public ChatLog(final String question, final String answer, final RepoAnalyze repoAnalyze) {
        this.question = question;
        this.answer = answer;
        this.sequence = 0L;
        this.repoAnalyze = repoAnalyze;
    }
}
