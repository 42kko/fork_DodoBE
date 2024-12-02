package dododocs.dododocs.analyze.dto;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FindRepoRegisterResponses {
    private List<RegisteredRepoResponse> registeredRepoResponses;

    private FindRepoRegisterResponses() {
    }

    public FindRepoRegisterResponses(final List<RepoAnalyze> repoAnalyzes) {
        this.registeredRepoResponses = toResponses(repoAnalyzes);
    }

    private List<RegisteredRepoResponse> toResponses(final List<RepoAnalyze> repoAnalyzes) {
        return repoAnalyzes.stream()
                .map(RegisteredRepoResponse::new)
                .collect(Collectors.toList());
    }
}
