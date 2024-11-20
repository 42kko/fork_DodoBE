package dododocs.dododocs.analyze.presentation;


import dododocs.dododocs.analyze.application.DownloadFromS3Service;
import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class S3DownloadController {
    private final DownloadFromS3Service s3DownloadService;

    @GetMapping("/download/s3")
    public String downloadAIAnalyzeResultFromS3(@Authentication final Accessor accessor) throws Exception {
        String bucketName = "haon-dododocs";
        String s3Key = "open-source";
        s3DownloadService.downloadAndProcessZip(bucketName, s3Key);
        return "ZIP 파일 다운로드 및 처리 완료!";
    }
}