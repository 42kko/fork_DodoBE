package dododocs.dododocs.analyze.application;

import com.amazonaws.services.s3.AmazonS3Client;
import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeResponse;
import dododocs.dododocs.analyze.dto.DownloadReadmeAnalyzeResponse;
import dododocs.dododocs.analyze.dto.EmptyFolderException;
import dododocs.dododocs.analyze.exception.NoExistRepoAnalyzeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@RequiredArgsConstructor
@Service
public class DownloadFromS3Service {
    private final RepoAnalyzeRepository repoAnalyzeRepository;
    private final AmazonS3Client amazonS3Client;
    private final String bucketName = "haon-dododocs";

    public DownloadAiAnalyzeResponse downloadAndProcessZipDocsInfo(final long registeredRepoId) throws IOException {
        final RepoAnalyze repoAnalyze = repoAnalyzeRepository.findById(registeredRepoId)
                .orElseThrow(() -> new NoExistRepoAnalyzeException("레포지토리 정보가 존재하지 않습니다."));

        final String s3Key = repoAnalyze.getDocsKey();

        // 1. S3에서 ZIP 파일 다운로드
        File zipFile = downloadZipFromS3(bucketName, s3Key);

        // 2. ZIP 파일 압축 해제
        File extractedDir = unzipFile(zipFile);

        // 3. .md 파일을 FileDetail 형식으로 변환하여 분류
        Map<String, List<DownloadAiAnalyzeResponse.FileDetail>> categorizedFiles = collectAndCategorizeMarkdownFiles(extractedDir);

        // 4. 임시 파일 삭제
        zipFile.delete();
        deleteDirectory(extractedDir);

        return new DownloadAiAnalyzeResponse(categorizedFiles.get("summary"), categorizedFiles.get("regular"));
    }

    public DownloadAiAnalyzeResponse downloadAndProcessZipDocsInfoTest(final long registeredRepoId) throws IOException {
        final RepoAnalyze repoAnalyze = repoAnalyzeRepository.findById(registeredRepoId)
                .orElseThrow(() -> new NoExistRepoAnalyzeException("레포지토리 정보가 존재하지 않습니다."));

        final String s3Key = repoAnalyze.getDocsKey();

        // 1. S3에서 ZIP 파일 다운로드
        File zipFile = downloadZipFromS3(bucketName, s3Key);

        // 2. ZIP 파일 압축 해제
        File extractedDir = unzipFile(zipFile);

        // 3. 폴더가 비어있을 경우 예외 처리
        if (extractedDir.listFiles() == null || extractedDir.listFiles().length == 0) {
            throw new EmptyFolderException("압축 해제된 폴더가 비어 있습니다.");
        }

        // 3. .md 파일을 FileDetail 형식으로 변환하여 분류
        Map<String, List<DownloadAiAnalyzeResponse.FileDetail>> categorizedFiles = collectAndCategorizeMarkdownFiles(extractedDir);

        // 4. 임시 파일 삭제
        zipFile.delete();
        deleteDirectory(extractedDir);

        return new DownloadAiAnalyzeResponse(categorizedFiles.get("summary"), categorizedFiles.get("regular"));
    }

    private File downloadZipFromS3(String bucketName, String s3Key) throws IOException {
        File tempZipFile = File.createTempFile("s3-download", ".zip");

        System.out.println("===============================================");
        System.out.println("bucketName:" + bucketName);
        System.out.println("s3key:" + s3Key);
        System.out.println("===============================================");

        try (InputStream inputStream = amazonS3Client.getObject(bucketName, "result/" + s3Key).getObjectContent();
             FileOutputStream outputStream = new FileOutputStream(tempZipFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new NoExistRepoAnalyzeException("레포지토리 결과물을 아직 생성중입니다. 잠시만 기다려주세요.");
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

    private Map<String, List<DownloadAiAnalyzeResponse.FileDetail>> collectAndCategorizeMarkdownFiles(File directory) throws IOException {
        List<DownloadAiAnalyzeResponse.FileDetail> summaryFiles = new ArrayList<>();
        List<DownloadAiAnalyzeResponse.FileDetail> regularFiles = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    Map<String, List<DownloadAiAnalyzeResponse.FileDetail>> subCategory = collectAndCategorizeMarkdownFiles(file);
                    summaryFiles.addAll(subCategory.get("summary"));
                    regularFiles.addAll(subCategory.get("regular"));
                } else if (file.getName().endsWith(".md")) {
                    DownloadAiAnalyzeResponse.FileDetail fileDetail = new DownloadAiAnalyzeResponse.FileDetail(file.getName(), readFileContent(file));

                    if (file.getName().contains("_summary")) {
                        summaryFiles.add(fileDetail);
                    } else {
                        regularFiles.add(fileDetail);
                    }
                }
            }
        }

        Map<String, List<DownloadAiAnalyzeResponse.FileDetail>> categorizedFiles = new HashMap<>();
        categorizedFiles.put("summary", summaryFiles);
        categorizedFiles.put("regular", regularFiles);

        return categorizedFiles;
    }

    private String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
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







    ///// // 리드미 업데이트
    public void updateFileContent(String repoName, String fileName, String newContent) throws IOException {
        // 1. 레포지토리 정보 확인
        RepoAnalyze repoAnalyze = repoAnalyzeRepository.findByRepositoryName(repoName)
                .orElseThrow(() -> new NoExistRepoAnalyzeException("레포지토리 정보가 존재하지 않습니다."));

        String s3Key = repoAnalyze.getDocsKey();

        // 2. S3에서 ZIP 파일 다운로드
        File zipFile = downloadZipFromS3(bucketName, s3Key);

        // 3. ZIP 파일 압축 해제
        File extractedDir = unzipFile(zipFile);

        // 4. 파일 내용 업데이트
        updateMarkdownFileContent(extractedDir, fileName, newContent);

        // 5. ZIP 파일 다시 생성 및 업로드
        File updatedZip = createZipFromDirectory(extractedDir);
        uploadZipToS3(bucketName, s3Key, updatedZip);

        // 6. 임시 파일 삭제
        zipFile.delete();
        updatedZip.delete();
        deleteDirectory(extractedDir);
    }

    private void updateMarkdownFileContent(File directory, String fileName, String newContent) throws IOException {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    updateMarkdownFileContent(file, fileName, newContent);
                } else if (file.getName().equals(fileName)) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write(newContent);
                    }
                    return;
                }
            }
        }
        throw new FileNotFoundException("파일을 찾을 수 없습니다: " + fileName);
    }

    private File createZipFromDirectory(File directory) throws IOException {
        File zipFile = File.createTempFile("updated", ".zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(bos)) {

            zipDirectory(directory, directory.getPath(), zos);
        }

        return zipFile;
    }

    private void zipDirectory(File folder, String basePath, java.util.zip.ZipOutputStream zos) throws IOException {
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipDirectory(file, basePath, zos);
                } else {
                    String zipEntryName = file.getPath().substring(basePath.length() + 1);
                    zos.putNextEntry(new java.util.zip.ZipEntry(zipEntryName));

                    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = bis.read(buffer)) != -1) {
                            zos.write(buffer, 0, read);
                        }
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    private void uploadZipToS3(String bucketName, String s3Key, File zipFile) {
        amazonS3Client.putObject(bucketName, s3Key, zipFile);
    }

    public DownloadAiAnalyzeResponse downloadAndProcessZipReadmeInfoByRepoName(final long repoId) throws IOException {
        final RepoAnalyze repoAnalyze = repoAnalyzeRepository.findById(repoId)
                .orElseThrow(() -> new NoExistRepoAnalyzeException("레포지토리 정보가 존재하지 않습니다."));

        System.out.println("========================123123123123 🔥");
        System.out.println(repoAnalyze.getBranchName());
        System.out.println(repoAnalyze.getRepoUrl());
        System.out.println(repoAnalyze.getReadMeKey());
        System.out.println(repoAnalyze.getRepositoryName());
        System.out.println("========================123123123123 🔥");

        final String s3Key = repoAnalyze.getDocsKey();

        // 1. S3에서 ZIP 파일 다운로드
        File zipFile = downloadZipFromS3(bucketName, s3Key);

        // 2. ZIP 파일 압축 해제
        File extractedDir = unzipFile(zipFile);

        // 3. .md 파일을 FileDetail 형식으로 변환하여 분류
        Map<String, List<DownloadAiAnalyzeResponse.FileDetail>> categorizedFiles = collectAndCategorizeMarkdownFiles(extractedDir);

        // 4. 임시 파일 삭제
        zipFile.delete();
        deleteDirectory(extractedDir);

        return new DownloadAiAnalyzeResponse(categorizedFiles.get("summary"), categorizedFiles.get("regular"));
    }


    public DownloadReadmeAnalyzeResponse downloadAndProcessZipReadmeInfo(final long repoRegisterId) throws IOException {
        // 1. 레포지토리 정보 확인
        RepoAnalyze repoAnalyze = repoAnalyzeRepository.findById(repoRegisterId)
                .orElseThrow(() -> new NoExistRepoAnalyzeException("레포지토리 정보가 존재하지 않습니다."));

        // 2. readMeKey 가져오기
        String readMeKey = repoAnalyze.getReadMeKey();
        if (readMeKey == null || readMeKey.isEmpty()) {
            throw new NoExistRepoAnalyzeException("readMeKey가 존재하지 않습니다.");
        }

        // 3. S3에서 파일 다운로드
        String readMeContent = downloadFileFromS3(bucketName, readMeKey);

        // 4. 결과 반환
        return new DownloadReadmeAnalyzeResponse(readMeContent);
    }

    private String downloadFileFromS3(String bucketName, String s3Key) throws IOException {
        try (InputStream inputStream = amazonS3Client.getObject(bucketName, "result/" + s3Key).getObjectContent();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString();
        } catch (Exception e) {
            throw new NoExistRepoAnalyzeException("레포지토리 결과물을 아직 생성중입니다. 잠시만 기다려주세요.");
        }
    }
}