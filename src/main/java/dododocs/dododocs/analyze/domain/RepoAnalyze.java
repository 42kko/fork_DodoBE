package dododocs.dododocs.analyze.domain;

import dododocs.dododocs.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "repo_analyze")
@Getter
@Entity
public class RepoAnalyze {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repository_name")
    private String repositoryName;

    @Column(name = "readme_key", nullable = false)
    private String readMeKey;

    @Column(name = "docs_key", nullable = true) // docs key 는 null 이 허용된다. (Java 파일이 없는 경우 null 이 나올 수 있음)
    private String docsKey;

    @Column(name = "repo_url", nullable = false)
    private String repoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    protected RepoAnalyze() {
    }

    public RepoAnalyze(final String repositoryName, final String readMeKey, final String docsKey, final String repoUrl, final Member member) {
        this.repositoryName = repositoryName;
        this.readMeKey = readMeKey;
        this.docsKey = docsKey;
        this.repoUrl = repoUrl;
        this.member = member;
    }
}
