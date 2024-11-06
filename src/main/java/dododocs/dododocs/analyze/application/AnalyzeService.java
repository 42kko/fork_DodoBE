package dododocs.dododocs.analyze.application;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class AnalyzeService {
    private final AmazonS3 amazonS3Client;
    private final String bucketName = "haon-dododocs"; // S3 버킷 이름

    public AnalyzeService(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public void uploadZipToS3(String fileName) {
        try {
            // 1. ZIP 파일 생성
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
                ZipEntry zipEntry = new ZipEntry("example.txt");  // 압축할 파일명 지정
                zipOut.putNextEntry(zipEntry);
                zipOut.write("This is an example file content.".getBytes());  // 파일 내용 작성
                zipOut.closeEntry();
            }

            // 2. S3에 업로드하기 위한 InputStream 생성
            byte[] zipBytes = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(zipBytes);

            // 3. 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(zipBytes.length);
            metadata.setContentType("application/zip");

            // 4. S3에 ZIP 파일 업로드
            amazonS3Client.putObject(bucketName, fileName + ".zip", inputStream, metadata);

            System.out.println("ZIP 파일 업로드 성공: " + fileName + ".zip");

        } catch (Exception e) {
            System.err.println("ZIP 파일 업로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
