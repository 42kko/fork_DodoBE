package dododocs.dododocs.member.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
import dododocs.dododocs.auth.exception.NoExistMemberException;
import dododocs.dododocs.member.domain.Member;
import dododocs.dododocs.member.dto.FindRepoNameListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MemberService(final MemberRepository memberRepository, final RestTemplate restTemplate, final ObjectMapper objectMapper) {
        this.memberRepository = memberRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public FindRepoNameListResponse getUserRepositories(final long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(NoExistMemberException::new);
        final String memberName = member.getOriginName();
        final String url = "https://api.github.com/users/" + memberName + "/repos";

        System.out.println("🔥 request url:" + url);

        // GitHub API 호출하여 레포지토리 목록 가져오기
        Object response = restTemplate.getForObject(url, Object.class);

        List<Map<String, Object>> repos = objectMapper.convertValue(response, new TypeReference<List<Map<String, Object>>>() {});

        return new FindRepoNameListResponse(repos.stream()
                    .map(repo -> (String) repo.get("name"))
                    .collect(Collectors.toList()));
    }
}
