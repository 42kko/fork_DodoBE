package dododocs.dododocs.auth.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dododocs.dododocs.analyze.domain.MemberOrganization;
import dododocs.dododocs.analyze.domain.repository.MemberOrganizationRepository;
import dododocs.dododocs.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class GithubOrganizationClient {
    private final MemberOrganizationRepository memberOrganizationRepository;
    private final RestTemplate restTemplate;

    public void saveMemberOrganizationNames(final Member member, final String username) throws Exception {
        String url = String.format("https://api.github.com/users/%s/orgs", username);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> orgs = objectMapper.readValue(response.getBody(), new TypeReference<List<Map<String, Object>>>() {});

        for (Map<String, Object> org : orgs) {
            String name = (String) org.get("login");
            memberOrganizationRepository.save(new MemberOrganization(member, name));
        }
    }
}
