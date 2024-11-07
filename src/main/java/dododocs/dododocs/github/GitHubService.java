package dododocs.dododocs.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

// 레포 통째로 받아오기
@Service
public class GitHubService {
    private final String GITHUB_API_URL = "https://api.github.com/repos";
    private final RestTemplate restTemplate;

    public GitHubService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getFileContent(String owner, String repo, String path, String token) throws JsonProcessingException {
        final String url = UriComponentsBuilder.fromHttpUrl(GITHUB_API_URL)
                .pathSegment(owner, repo, "contents", path)
                .queryParam("ref", "main")  // 브랜치가 main일 경우 명시적으로 지정
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        String responseBody = response.getBody();

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // Base64 인코딩된 파일 내용 추출
        String encodedContent = jsonNode.get("content").asText().replaceAll("\n", "").replaceAll("\r", "");
        byte[] decodedBytes = Base64.getDecoder().decode(encodedContent);
        String fileContent = new String(decodedBytes, StandardCharsets.UTF_8);

        return response.getBody();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 재귀적으로 폴더 내 모든 파일과 폴더를 읽어오는 메서드
    public List<String> getAllContents(String owner, String repo, String path, String branch, String token) throws Exception {
        List<String> folderContents = new ArrayList<>();
        readContentsRecursively(owner, repo, path, branch, token, folderContents);
        return folderContents;
    }

    // 실제 폴더 내용을 읽고, 재귀적으로 하위 폴더도 읽어오는 메서드
    private void readContentsRecursively(String owner, String repo, String path, String branch, String token, List<String> folderContents) throws Exception {
        String url = UriComponentsBuilder.fromHttpUrl(GITHUB_API_URL)
                .pathSegment(owner, repo, "contents", path)
                .queryParam("ref", branch)  // 브랜치를 ref 파라미터로 지정
                .toUriString();

        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String responseBody = response.getBody();

        // JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // JSON 응답이 배열인지 확인
        if (jsonNode.isArray()) {
            // 각 파일/폴더 정보 확인
            for (JsonNode node : jsonNode) {
                // null 체크 추가
                JsonNode typeNode = node.get("type");
                JsonNode nameNode = node.get("name");
                JsonNode pathNode = node.get("path");

                if (typeNode != null && nameNode != null && pathNode != null) {
                    String type = typeNode.asText();
                    String name = nameNode.asText();
                    String filePath = pathNode.asText();

                    if ("file".equals(type)) {
                        // 파일인 경우 리스트에 추가
                        System.out.println("File: " + name + " (" + filePath + ")");
                        folderContents.add("File: " + name + " (" + filePath + ")");
                    } else if ("dir".equals(type)) {
                        // 폴더인 경우 리스트에 추가하고, 재귀적으로 하위 폴더 내용 읽기
                        System.out.println("Directory: " + name + " (" + filePath + ")");
                        folderContents.add("Directory: " + name + " (" + filePath + ")");
                        // 재귀적으로 하위 폴더 내용을 가져옴
                        readContentsRecursively(owner, repo, filePath, branch, token, folderContents);
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Expected JSON array but received: " + jsonNode.toString());
        }
    }
}
