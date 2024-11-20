
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
@RequestMapping("/api")
public class AnalyzeController {
    private final AnalyzeService analyzeService;

    public AnalyzeController(final AnalyzeService analyzeService) {
        this.analyzeService = analyzeService;
    }

    @GetMapping("/upload/s3")
    public String uploadGithubToS3(@Authentication final Accessor accessor,
                                   @RequestBody final UploadGitRepoContentToS3Request uploadToS3Request) {
        // s3 key 값, 레포 주소 필요
        try {
            System.out.println(uploadToS3Request.getRepositoryName());
            analyzeService.uploadGithubRepoToS3(accessor.getId(), uploadToS3Request.getRepositoryName(), uploadToS3Request.getBranchName());
            return "GitHub repository successfully uploaded to S3!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to upload GitHub repository to S3: " + e.getMessage();
        }
    }

    @GetMapping("/download/s3")
    public void downloadAIAnalyzeResultFromS3(@Authentication final Accessor accessor) {
        
    }
}

