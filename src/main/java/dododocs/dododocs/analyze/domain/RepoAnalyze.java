package dododocs.dododocs.analyze.domain;

import dododocs.dododocs.chatbot.domain.ChatLog;
import dododocs.dododocs.global.BaseEntity;
import dododocs.dododocs.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Table(name = "repo_analyze")
@Getter
@Entity
@Setter
public class RepoAnalyze extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repository_name")
    private String repositoryName;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "readme_key", nullable = false)
    private String readMeKey;

    @Column(name = "docs_key", nullable = true) // docs key 는 null 이 허용된다. (Java 파일이 없는 경우 null 이 나올 수 있음)
    private String docsKey;

    @Column(name = "repo_url", nullable = false)
    private String repoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "docs_completed")
    private boolean docsCompleted = false;

    @Column(name = "readme_completed")
    private boolean readmeCompleted = false;

    @Column(name = "chatbot_completed")
    private boolean chatbotCompleted = false;

    @Column(name = "analyzed")
    private boolean analyzed = false;

    @OneToMany(mappedBy = "repoAnalyze", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLog> chatLogs = new ArrayList<>();

    protected RepoAnalyze() {
    }

    public RepoAnalyze(final String repositoryName, final String branchName, final String readMeKey, final String docsKey, final String repoUrl, final Member member) {
        this.repositoryName = repositoryName;
        this.branchName = branchName;
        this.readMeKey = readMeKey;
        this.docsKey = docsKey;
        this.repoUrl = repoUrl;
        this.member = member;
    }

    public RepoAnalyze(final long id, final String repositoryName, final String branchName, final String readMeKey, final String docsKey, final String repoUrl, final Member member) {
        this.id = id;
        this.repositoryName = repositoryName;
        this.branchName = branchName;
        this.readMeKey = readMeKey;
        this.docsKey = docsKey;
        this.repoUrl = repoUrl;
        this.member = member;
    }

    public RepoAnalyze(final long id, final String repositoryName) {
        this.id = id;
        this.repositoryName = repositoryName;
    }
}
