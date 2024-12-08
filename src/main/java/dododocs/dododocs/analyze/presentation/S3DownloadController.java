package dododocs.dododocs.analyze.presentation;


import dododocs.dododocs.analyze.application.DownloadFromS3Service;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeRequest;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeResponse;
import dododocs.dododocs.analyze.dto.FileContentResponse;
import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RequestMapping("/api/download")
@RestController
@RequiredArgsConstructor
public class S3DownloadController {
    private final DownloadFromS3Service s3DownloadService;

    @PostMapping("/s3")
    public DownloadAiAnalyzeResponse downloadAIAnalyzeResultFromS3(@RequestParam final String repositoryName) throws Exception {
        return s3DownloadService.downloadAndProcessZip(repositoryName);
    }

    @GetMapping("/s3/detail")
    public FileContentResponse getFileContentByFileName(@RequestParam final String repositoryName,
                                                        @RequestParam final String fileName) throws Exception {
        DownloadAiAnalyzeResponse response = s3DownloadService.downloadAndProcessZip(repositoryName);

        // 검색 로직
        return response.getSummaryFiles().stream()
                .filter(file -> fileName.equals(file.getFileName()))
                .findFirst()
                .or(() -> response.getRegularFiles().stream()
                        .filter(file -> fileName.equals(file.getFileName()))
                        .findFirst())
                .map(file -> new FileContentResponse(file.getFileName(), file.getFileContents()))
                .orElseThrow(() -> new RuntimeException("File not found"));
    }
}