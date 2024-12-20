package dododocs.dododocs.analyze.application;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.analyze.dto.FindRepoRegisterResponses;
import dododocs.dododocs.analyze.dto.UpdateChatbotDocsRepoAnalyzeReadyStatusRequest;
import dododocs.dododocs.analyze.dto.UpdateReadmeDocsRepoAnalyzeReadyStatusRequest;
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

    public void updateReadmeDocsRepoAnalyzeReadyStatus(final UpdateReadmeDocsRepoAnalyzeReadyStatusRequest updateRepoAnalyzeReadyStatusRequest) {
        final RepoAnalyze repoAnalyze = findByRepoUrl(updateRepoAnalyzeReadyStatusRequest.getRepoUrl());

        final boolean isDocsCOmpleted = false;

        repoAnalyzeRepository.save(
                new RepoAnalyze(
                        repoAnalyze.getId(),
                        repoAnalyze.getRepositoryName(),
                        repoAnalyze.getBranchName(),
                        repoAnalyze.getReadMeKey(),
                        repoAnalyze.getDocsKey(),
                        repoAnalyze.getRepoUrl(),
                        repoAnalyze.getMember(),

                        updateRepoAnalyzeReadyStatusRequest.isDocsCompleted(),
                        updateRepoAnalyzeReadyStatusRequest.isReadmeCompleted(),
                        repoAnalyze.isChatbotCompleted()
                )
        );
    }

    public void updateChatbotRepoAnalyzeReadyStatus(final UpdateChatbotDocsRepoAnalyzeReadyStatusRequest updateRepoAnalyzeReadyStatusRequest) {
        final RepoAnalyze repoAnalyze = findByRepoUrl(updateRepoAnalyzeReadyStatusRequest.getRepoUrl());

        repoAnalyze.setChatbotCompleted(updateRepoAnalyzeReadyStatusRequest.isChatbotCompleted());
        repoAnalyzeRepository.save(repoAnalyze);

        repoAnalyzeRepository.save(
                new RepoAnalyze(
                        repoAnalyze.getId(),
                        repoAnalyze.getRepositoryName(),
                        repoAnalyze.getBranchName(),
                        repoAnalyze.getReadMeKey(),
                        repoAnalyze.getDocsKey(),
                        repoAnalyze.getRepoUrl(),
                        repoAnalyze.getMember(),
                        repoAnalyze.isDocsCompleted(),
                        repoAnalyze.isReadmeCompleted(),
                        updateRepoAnalyzeReadyStatusRequest.isChatbotCompleted()
                )
        );
    }

    private RepoAnalyze findByRepoUrl(final String repoUrl) {
        final String[] parts = repoUrl.split("/");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid repoUrl format: " + repoUrl);
        }

        final String repoName = parts[4];
        final String branchName = parts[5];

        System.out.println("repoName: +" + repoName);
        System.out.println("branchName: +" + branchName);

        return repoAnalyzeRepository.findByRepositoryNameAndBranchName(repoName, branchName)
                .orElseThrow(() -> new IllegalArgumentException("No RepoAnalyze found for repoName: " + repoName + " and branchName: " + branchName));
    }
}
