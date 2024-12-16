package dododocs.dododocs.analyze.presentation;


import dododocs.dododocs.analyze.application.DownloadFromS3Service;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeRequest;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeResponse;
import dododocs.dododocs.analyze.dto.DownloadReadmeAnalyzeResponse;
import dododocs.dododocs.analyze.dto.FileContentResponse;
import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class S3DownloadController {
    private final DownloadFromS3Service s3DownloadService;
    private final DownloadFromS3Service downloadFromS3Service;

    @PostMapping("/download/docs/{registeredRepoId}")
    public ResponseEntity<DownloadAiAnalyzeResponse> downloadAIDocumentAnalyzeResultFromS3(@Authentication final Accessor accessor,
                                                                      @PathVariable final Long registeredRepoId) throws Exception {
        return ResponseEntity.ok(s3DownloadService.downloadAndProcessZipDocsInfo(registeredRepoId));
    }

    @PostMapping("/download/readme/{registeredRepoId}")
    public ResponseEntity<DownloadReadmeAnalyzeResponse> downloadReadmeFromS3(@Authentication final Accessor accessor,
                                                                              @PathVariable final Long registeredRepoId) throws Exception {
        return ResponseEntity.ok(s3DownloadService.downloadAndProcessZipReadmeInfo(registeredRepoId));
    }

    @GetMapping("/download/s3/detail/{registeredRepoId}")
    public FileContentResponse getFileContentByFileName(@PathVariable final Long registeredRepoId,
                                                        @RequestParam final String fileName) throws Exception {
        DownloadAiAnalyzeResponse response = s3DownloadService.downloadAndProcessZipReadmeInfoByRepoName(registeredRepoId);

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

    @PutMapping("/readme")
    public ResponseEntity<Void> updateFileContent(
            @Authentication final Accessor accessor,
            @RequestParam String repositoryName, @RequestParam String fileName, @RequestParam String newContent) throws Exception {
        downloadFromS3Service.updateFileContent(repositoryName, fileName, newContent);
        return ResponseEntity.noContent().build();
    }
}