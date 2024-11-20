package dododocs.dododocs.analyze.application;

import com.amazonaws.services.s3.AmazonS3Client;
import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.analyze.exception.NoExistRepoAnalyzeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RequiredArgsConstructor
@Service
public class DownloadFromS3Service {
    private final RepoAnalyzeRepository repoAnalyzeRepository;
    private final AmazonS3Client amazonS3Client;
    private final String bucketName = "haon-dododocs";

    public void downloadAndProcessZip(final String repoName) throws IOException {
        final RepoAnalyze repoAnalyze = repoAnalyzeRepository.findByRepositoryName(repoName)
                .orElseThrow(() -> new NoExistRepoAnalyzeException("레포지토리 정보가 존재하지 않습니다."));

        final String s3Key = repoAnalyze.getDocsKey();

        // 1. S3에서 ZIP 파일 다운로드
        File zipFile = downloadZipFromS3(bucketName, s3Key);

        // 2. ZIP 파일 압축 해제
        File extractedDir = unzipFile(zipFile);

        // 3. .md 파일 내용 출력
        printMarkdownFiles(extractedDir);

        // 4. 임시 파일 삭제
        zipFile.delete();
        deleteDirectory(extractedDir);
    }

    private File downloadZipFromS3(String bucketName, String s3Key) throws IOException {
        File tempZipFile = File.createTempFile("s3-download", ".zip");

        try (InputStream inputStream = amazonS3Client.getObject(bucketName, s3Key).getObjectContent();
             FileOutputStream outputStream = new FileOutputStream(tempZipFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return tempZipFile;
    }

    private File unzipFile(File zipFile) throws IOException {
        File outputDir = new File(zipFile.getParent(), "extracted");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(outputDir, entry.getName());

                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }

        return outputDir;
    }

    private void printMarkdownFiles(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    printMarkdownFiles(file); // 재귀적으로 하위 디렉토리 탐색
                } else if (file.getName().endsWith(".md")) {
                    System.out.println("File: " + file.getName());
                    System.out.println("Content:");
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    }
                    System.out.println("-------------------------------");
                }
            }
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}
