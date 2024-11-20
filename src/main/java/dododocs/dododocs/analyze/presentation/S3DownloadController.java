package dododocs.dododocs.analyze.presentation;


import dododocs.dododocs.analyze.application.DownloadFromS3Service;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeRequest;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeResponse;
import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class S3DownloadController {
    private final DownloadFromS3Service s3DownloadService;

    @GetMapping("/download/s3")
    public DownloadAiAnalyzeResponse downloadAIAnalyzeResultFromS3(@Authentication final Accessor accessor,
                                                                   @RequestBody final DownloadAiAnalyzeRequest downloadAiAnalyzeRequest) throws Exception {
        return s3DownloadService.downloadAndProcessZip(downloadAiAnalyzeRequest.getRepositoryName());
    }
}