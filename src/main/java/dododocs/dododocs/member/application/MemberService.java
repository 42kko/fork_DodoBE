package dododocs.dododocs.member.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> getUserRepositories(String username) {
        String url = "https://api.github.com/users/" + username + "/repos";
        try {
            // GitHub API 호출하여 레포지토리 목록 가져오기
            Object response = restTemplate.getForObject(url, Object.class);

            // 응답을 List<Map>으로 변환
            List<Map<String, Object>> repos = objectMapper.convertValue(response, new TypeReference<List<Map<String, Object>>>() {});

            // 레포지토리 이름 리스트 추출
            return repos.stream()
                    .map(repo -> (String) repo.get("name"))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("레포지토리 목록을 가져오는 중 오류 발생: " + e.getMessage());
            return List.of();
        }
    }
}
