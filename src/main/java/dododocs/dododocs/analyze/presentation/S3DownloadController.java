package dododocs.dododocs.analyze.presentation;


import dododocs.dododocs.analyze.application.DownloadFromS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class S3DownloadController {

    private final DownloadFromS3Service s3DownloadService;

    @GetMapping("/process-s3-zip")
    public String processS3Zip() {
        try {
            String bucketName = "haon-dododocs";
            String s3Key = "open-source";
            s3DownloadService.downloadAndProcessZip(bucketName, s3Key);
            return "ZIP 파일 다운로드 및 처리 완료!";
        } catch (Exception e) {
            e.printStackTrace();
            return "처리 중 오류 발생: " + e.getMessage();
        }
    }
}