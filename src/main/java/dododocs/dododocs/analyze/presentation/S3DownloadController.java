package dododocs.dododocs.analyze.presentation;


import dododocs.dododocs.analyze.application.DownloadFromS3Service;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeRequest;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeResponse;
import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class S3DownloadController {
    private final DownloadFromS3Service s3DownloadService;

    @PostMapping("/download/s3")
    public DownloadAiAnalyzeResponse downloadAIAnalyzeResultFromS3(@RequestBody final DownloadAiAnalyzeRequest downloadAiAnalyzeRequest) throws Exception {
        return s3DownloadService.downloadAndProcessZip(downloadAiAnalyzeRequest.getRepositoryName());
    }
}