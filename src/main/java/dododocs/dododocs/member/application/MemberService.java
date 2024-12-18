package dododocs.dododocs.member.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dododocs.dododocs.analyze.application.AnalyzeService;
import dododocs.dododocs.analyze.application.DownloadFromS3Service;
import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
import dododocs.dododocs.auth.exception.NoExistMemberException;
import dododocs.dododocs.chatbot.application.ChatbotService;
import dododocs.dododocs.chatbot.dto.FindMemberInfoResponse;
import dododocs.dododocs.member.domain.Member;
import dododocs.dododocs.member.dto.FindRegisterMemberRepoResponses;
import dododocs.dododocs.member.dto.FindRegisterRepoResponse;
import dododocs.dododocs.member.dto.FindRepoNameListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final RepoAnalyzeRepository repoAnalyzeRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final DownloadFromS3Service downloadFromS3Service; // downloadAndProcessZipReadmeInfo, downloadAndProcessZipDocsInfo
    private final ChatbotService chatbotService; // questionToChatbotAndSaveLogs

    public FindRepoNameListResponse getUserRepositories(final long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(NoExistMemberException::new);
        final String memberName = member.getOriginName();
        final String url = "https://api.github.com/users/" + memberName + "/repos";

        // GitHub API 호출하여 레포지토리 목록 가져오기
        Object response = restTemplate.getForObject(url, Object.class);

        List<Map<String, Object>> repos = objectMapper.convertValue(response, new TypeReference<List<Map<String, Object>>>() {});

        return new FindRepoNameListResponse(repos.stream()
                    .map(repo -> (String) repo.get("name"))
                    .collect(Collectors.toList()));
    }

    public FindRegisterMemberRepoResponses findRegisterMemberRepoResponses(final long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(NoExistMemberException::new);

        final List<RepoAnalyze> repoAnalyzes = repoAnalyzeRepository.findByMember(member);
        return new FindRegisterMemberRepoResponses(findRepoRegisteredCompleteStatus(repoAnalyzes));
    }

    private List<FindRegisterRepoResponse> findRepoRegisteredCompleteStatus(final List<RepoAnalyze> repoAnalyzes) {
        List<FindRegisterRepoResponse> findRegisterRepoResponses = new ArrayList<>();

        for (final RepoAnalyze repoAnalyze : repoAnalyzes) {
            final long registeredRepoId = repoAnalyze.getId();
            FindRegisterRepoResponse response = new FindRegisterRepoResponse(repoAnalyze);

            try {
                chatbotService.questionToChatbotAndSaveLogs(registeredRepoId, "dummy-question");
                response.setChatbotComplete(true);
            } catch (Exception e) {
                response.setChatbotComplete(false);
            }

            System.out.println("===========1111");

            try {
                downloadFromS3Service.downloadAndProcessZipReadmeInfo(registeredRepoId);
                response.setReadmeComplete(true);
            } catch (Exception e) {
                response.setReadmeComplete(false);
            }

            System.out.println("===========2222");

            try {
                downloadFromS3Service.downloadAndProcessZipDocsInfo(registeredRepoId);
                response.setDocsComplete(true);
            } catch (Exception e) {
                response.setDocsComplete(false);
            }

            System.out.println("===========3333");

            findRegisterRepoResponses.add(response);
        }

        return findRegisterRepoResponses;
    }


    public FindMemberInfoResponse findMemberInfo(final long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(NoExistMemberException::new);

        return new FindMemberInfoResponse(member.getOriginName());
    }
}
