package dododocs.dododocs.member.dto;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class FindRegisterRepoResponse {
    private String repositoryName;
    private String branchName;
    private LocalDate createdAt;

    private FindRegisterRepoResponse() {
    }

    public FindRegisterRepoResponse(final RepoAnalyze repoAnalyze) {
        this.repositoryName = repoAnalyze.getRepositoryName();
        this.branchName = repoAnalyze.getBranchName();

        if(repoAnalyze.getCreatedAt() != null) {
            this.createdAt = LocalDate.from(repoAnalyze.getCreatedAt());
        } else {
            this.createdAt = LocalDate.now();
        }
    }
}
