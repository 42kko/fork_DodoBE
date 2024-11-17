
package dododocs.dododocs.analyze.presentation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dododocs.dododocs.analyze.application.AnalyzeService;
import dododocs.dododocs.analyze.dto.UploadGitRepoContentToS3Request;
import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/download")
public class AnalyzeController {
    private final AnalyzeService analyzeService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String githubApiUrl = "https://api.github.com/repos/{owner}/{repo}/contents/{path}";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalyzeController(final AnalyzeService analyzeService) {
        this.analyzeService = analyzeService;
    }

    @GetMapping("/github-folder")
    public ResponseEntity<Resource> downloadGithubFolderAsZip() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
            String owner = "msung99";
            String repo = "Gatsby-Starter-Haon";
            String path = ""; // 예: src/main/resources
            addFolderToZip(owner, repo, path, zipOut, "");
        }

        ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"github_folder.zip\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private void addFolderToZip(String owner, String repo, String path, ZipOutputStream zipOut, String parentPath) throws Exception {
        String url = githubApiUrl.replace("{owner}", owner)
                .replace("{repo}", repo)
                .replace("{path}", path);

        Object response = restTemplate.getForObject(url, Object.class);

        if (response instanceof List) {
            List<Map<String, Object>> fileList = objectMapper.convertValue(response, new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> fileData : fileList) {
                String type = (String) fileData.get("type");
                String name = (String) fileData.get("name");
                String downloadUrl = (String) fileData.get("download_url");
                String filePath = parentPath + name;

                if ("file".equals(type) && downloadUrl != null) {
                    System.out.println("Adding file to ZIP: " + filePath + " from " + downloadUrl);
                    try (InputStream inputStream = new URL(downloadUrl).openStream()) {
                        zipOut.putNextEntry(new ZipEntry(filePath));
                        inputStream.transferTo(zipOut);
                        zipOut.closeEntry();
                    } catch (Exception e) {
                        System.err.println("파일을 추가하는 중 오류 발생: " + filePath);
                        e.printStackTrace();
                    }
                } else if ("dir".equals(type)) {
                    System.out.println("Entering directory: " + filePath);
                    addFolderToZip(owner, repo, path + "/" + name, zipOut, filePath + "/");
                }
            }
        } else {
            System.out.println("응답이 파일 목록이 아닙니다.");
        }
    }

    @GetMapping("/upload/s3")
    public String uploadGithubToS3(@Authentication final Accessor accessor,
                                   @RequestBody final UploadGitRepoContentToS3Request uploadToS3Request) {

        String owner = "msung99";   // => gitHub 사용자명 또는 조직명
        String repo = "Gatsby-Starter-Haon";   // => 레포지토리 이름
        String branch = "main";      // => main
        String bucketName = "haon-dododocs";
        String s3Key = "open-source";

        try {
            System.out.println("===========");
            System.out.println(uploadToS3Request.getRepositoryName());
            analyzeService.uploadGithubRepoToS3(accessor.getId(), uploadToS3Request.getRepositoryName(), uploadToS3Request.getBranchName());
            return "GitHub repository successfully uploaded to S3!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to upload GitHub repository to S3: " + e.getMessage();
        }
    }
}

