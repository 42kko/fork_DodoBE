package dododocs.dododocs.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dododocs.dododocs.analyze.domain.RepoAnalyze;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class FindRegisterRepoResponse {
    private Long registeredRepoId;
    private String repositoryName;
    private String branchName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
    private boolean readmeComplete;
    private boolean chatbotComplete;
    private boolean docsComplete;

    private FindRegisterRepoResponse() {
    }

    public FindRegisterRepoResponse(final RepoAnalyze repoAnalyze) {
        this.registeredRepoId = repoAnalyze.getId();
        this.repositoryName = repoAnalyze.getRepositoryName();
        this.branchName = repoAnalyze.getBranchName();

        if(repoAnalyze.getCreatedAt() != null) {
            System.out.println("qqwihueqwhiuehqiwuehiuqwheiuqwhgiuyeqhgwiuyehiuqwhiueqwe");
            this.createdAt = LocalDate.from(repoAnalyze.getCreatedAt());
        } else {
            System.out.println("ojiqwejqwjioejqiowejioqwjeoiqwjoievn");
            this.createdAt = LocalDate.now();
        }
    }
}
