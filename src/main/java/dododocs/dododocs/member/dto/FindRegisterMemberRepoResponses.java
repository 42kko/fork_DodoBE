package dododocs.dododocs.member.dto;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FindRegisterMemberRepoResponses {
    private List<FindRegisterRepoResponse> findRegisterRepoResponses;

    private FindRegisterMemberRepoResponses() {
    }

    public FindRegisterMemberRepoResponses(final List<RepoAnalyze> repoAnalyzes) {
        this.findRegisterRepoResponses = toResponses(repoAnalyzes);
    }

    private List<FindRegisterRepoResponse> toResponses(final List<RepoAnalyze> repoAnalyzes) {
        return repoAnalyzes.stream()
                .map(FindRegisterRepoResponse::new)
                .collect(Collectors.toList());
    }
}
