package dododocs.dododocs.member.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dododocs.dododocs.analyze.application.AnalyzeService;
import dododocs.dododocs.analyze.application.DownloadFromS3Service;
import dododocs.dododocs.analyze.domain.MemberOrganization;
import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.domain.repository.MemberOrganizationRepository;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.analyze.dto.EmptyFolderException;
import dododocs.dododocs.analyze.exception.NoExistRepoAnalyzeException;
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
// @Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberOrganizationRepository memberOrganizationRepository;
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

        final List<String> memberOrganizations = memberOrganizationRepository.findOrganizationNamesByMember(member);

        // 조직 이름으로도 레포지토리 검색
        List<Map<String, Object>> organizationRepos = memberOrganizations.stream()
                .flatMap(orgName -> {
                    String orgUrl = "https://api.github.com/orgs/" + orgName + "/repos";
                    Object orgResponse = restTemplate.getForObject(orgUrl, Object.class);
                    return objectMapper.convertValue(orgResponse, new TypeReference<List<Map<String, Object>>>() {}).stream();
                })
                .collect(Collectors.toList());

        // 사용자 레포와 조직 레포 병합
        List<Map<String, Object>> combinedRepos = new ArrayList<>(repos);
        combinedRepos.addAll(organizationRepos);

        return new FindRepoNameListResponse(combinedRepos.stream()
                .map(repo -> (String) repo.get("name"))
                .distinct()
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

            response.setReadmeComplete(repoAnalyze.isReadmeCompleted());
            response.setDocsComplete(repoAnalyze.isDocsCompleted());
            response.setChatbotComplete(repoAnalyze.isChatbotCompleted());

            findRegisterRepoResponses.add(response);
        }

        return findRegisterRepoResponses;
    }


    /* private List<FindRegisterRepoResponse> findRepoRegisteredCompleteStatus(final List<RepoAnalyze> repoAnalyzes) {
        List<FindRegisterRepoResponse> findRegisterRepoResponses = new ArrayList<>();

        for (final RepoAnalyze repoAnalyze : repoAnalyzes) {
            final long registeredRepoId = repoAnalyze.getId();
            FindRegisterRepoResponse response = new FindRegisterRepoResponse(repoAnalyze);

            if(!repoAnalyze.isAnalyzed()) {
                try {
                    chatbotService.questionToChatbotAndSaveLogs(registeredRepoId, "레포지토리 정보 좀 요약해서 알려줄래?");
                    response.setChatbotComplete(true);
                } catch (RuntimeException e) {
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
                    downloadFromS3Service.downloadAndProcessZipDocsInfoTest(registeredRepoId);
                    response.setDocsComplete(true);
                } catch (EmptyFolderException e) {
                    response.setDocsComplete(true);
                } catch (Exception e) {
                    response.setDocsComplete(false);
                }

                System.out.println("===========3333");

                if(response.isDocsComplete() && response.isReadmeComplete() && response.isChatbotComplete()) {
                    System.out.println("wwwwwwwwwwwwwwwwwqwoijejqoiwfnnfnfnfnfnfnwq");
                    repoAnalyze.setAnalyzed(true);
                    repoAnalyzeRepository.save(repoAnalyze);
                }

                findRegisterRepoResponses.add(response);
            } else {
                System.out.println("=========gtyewrwerwerwebbggg");
                response.setDocsComplete(true);
                response.setReadmeComplete(true);
                response.setChatbotComplete(true);
                findRegisterRepoResponses.add(response);
            }
        }

        return findRegisterRepoResponses;
    } */


    public FindMemberInfoResponse findMemberInfo(final long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(NoExistMemberException::new);

        return new FindMemberInfoResponse(member.getOriginName());
    }
}
