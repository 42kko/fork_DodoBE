package dododocs.dododocs.analyze.dto;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import lombok.Getter;

@Getter
public class RegisteredRepoResponse {
    private long registeredRepoId;
    private String repositoryName;
    private String branchName;

    private RegisteredRepoResponse() {
    }

    public RegisteredRepoResponse(final RepoAnalyze repoAnalyze) {
        this.registeredRepoId = repoAnalyze.getId();
        this.repositoryName = repoAnalyze.getRepositoryName();
        this.branchName = repoAnalyze.getBranchName();
    }
}
