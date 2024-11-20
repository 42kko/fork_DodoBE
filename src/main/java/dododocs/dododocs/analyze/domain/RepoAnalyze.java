package dododocs.dododocs.analyze.domain;

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

    @Column(name = "readme_key")
    private String readMeKey;

    @Column(name = "docs_key")
    private String docsKey;

    protected RepoAnalyze() {
    }

    public RepoAnalyze(final String repositoryName, final String readMeKey, final String docsKey) {
        this.repositoryName = repositoryName;
        this.readMeKey = readMeKey;
        this.docsKey = docsKey;
    }
}
