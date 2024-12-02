package dododocs.dododocs.analyze.application;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.analyze.dto.FindRepoRegisterResponses;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
import dododocs.dododocs.auth.exception.NoExistMemberException;
import dododocs.dododocs.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RepoRegisterService {
    private final RepoAnalyzeRepository repoAnalyzeRepository;
    private final MemberRepository memberRepository;

    public FindRepoRegisterResponses findRegisteredRepos(final long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(NoExistMemberException::new);

        final List<RepoAnalyze> repoAnalyzes = repoAnalyzeRepository.findByMember(member);
        return new FindRepoRegisterResponses(repoAnalyzes);
    }

    public void removeRegisteredRepos(final long registeredRepoId) {
        repoAnalyzeRepository.deleteById(registeredRepoId);
    }
}
