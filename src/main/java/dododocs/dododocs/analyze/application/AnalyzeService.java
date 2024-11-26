package dododocs.dododocs.analyze.application;

import aj.org.objectweb.asm.TypeReference;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.domain.repository.MemberOrganizationRepository;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeRequest;
import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeResponse;
import dododocs.dododocs.analyze.dto.RepositoryContentDto;
import dododocs.dododocs.analyze.infrastructure.ExternalAiZipAnalyzeClient;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
import dododocs.dododocs.auth.exception.NoExistMemberException;
import dododocs.dododocs.member.domain.Member;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLConnection;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
@Service
public class AnalyzeService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final ExternalAiZipAnalyzeClient externalAiZipAnalyzeClient;
    private final MemberRepository memberRepository;
    private final AmazonS3Client amazonS3Client;
    private final MemberOrganizationRepository memberOrganizationRepository;
    private final RepoAnalyzeRepository repoAnalyzeRepository;

    // GitHub 레포지토리를 ZIP 파일로 가져와 S3에 업로드
    public void uploadGithubRepoToS3(final long memberId, String repoName, String branchName) {
        // Member를 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NoExistMemberException::new);

        // 개인 소유자로 먼저 시도
        String ownerName = member.getOriginName();
        boolean success = tryUploadFromOwner(ownerName, repoName, branchName);

        // 개인 소유에서 찾지 못하면 조직 소유로 검색
        if (!success) {
            List<String> organizationNames = findOrganizationNames(member);
            for (String orgName : organizationNames) {
                success = tryUploadFromOwner(orgName, repoName, branchName);
                if (success) {
                    break;
                }
            }
        }

        String s3Key = ownerName + "-" + repoName;
        String repoUrl = String.format("https://github.com/%s/%s", ownerName, repoName);

        ExternalAiZipAnalyzeResponse externalAiZipAnalyzeResponse =
                externalAiZipAnalyzeClient.requestAiZipDownloadAndAnalyze(new ExternalAiZipAnalyzeRequest
                        (s3Key, String.format("https://github.com/%s/%s", ownerName, repoName), false));

        // 1. readMeS3Key / 2. docsS3Key

        // DB 에 레포 정보 저장할 엔티티 생성
        // 1. readmeKey (AI 가 만들어준 s3 내의 레포 분석 결과인 ZIP 파일이 어디있는지)
        // 2. docsS3key (AI 가 만들어준 s3 내의 레포 분석 결과인 ZIP 파일이 어디있는지)
        // 3. repositoryName (ex. Gatsby-Starter-Haon)
        // 4. ownerName (ex. msung99)
        repoAnalyzeRepository.save(
                new RepoAnalyze(repoName,
                        externalAiZipAnalyzeResponse.getReadMeS3Key(),
                        externalAiZipAnalyzeResponse.getDocsS3Key(),
                        repoUrl,
                        member)
        );
    }

    // 특정 소유자(개인 또는 조직)에서 레포지토리를 찾아 업로드 시도
    private boolean tryUploadFromOwner(String ownerName, String repoName, String branchName) {
        String bucketDetailName = ownerName + "-" + repoName;
        String downloadUrl = String.format("https://github.com/%s/%s/archive/refs/heads/%s.zip", ownerName, repoName, branchName);
        System.out.println("Attempting to download from GitHub URL: " + downloadUrl);

        // ZIP 파일을 임시 디렉토리에 저장
        File tempFile = null;
        try {
            tempFile = File.createTempFile(repoName, ".zip");
            downloadFileFromUrl(downloadUrl, tempFile);
        } catch (Exception e) {
            System.out.println("깃허브 레포지토리 다운로드 받다가 오류터짐");
            System.out.println("===================");
            System.out.println(e.getMessage());
            System.out.println("===================");
            System.out.println(e.getCause());
        }
        // S3에 업로드
        try {
            amazonS3Client.putObject("haon-dododocs", bucketDetailName, tempFile);
        } catch (Exception e) {
            System.out.println("s3 에 업로드하다가 애러터짐");
            System.out.println("===================");
            System.out.println(e.getMessage());
            System.out.println("===================");
            System.out.println(e.getCause());
        }

        // 업로드 후 임시 파일 삭제
        tempFile.delete();
        return true;
    }

    // URL에서 파일 다운로드
    private void downloadFileFromUrl(String downloadUrl, File destinationFile) throws IOException {
        URL url = new URL(downloadUrl);

        URLConnection connection;
        String activeProfile = System.getProperty("spring.profiles.active", "default");

        // dev 또는 prod 프로파일에만 프록시 설정
        if ("dev".equals(activeProfile) || "prod".equals(activeProfile)) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("krmp-proxy.9rum.cc", 3128));
            connection = url.openConnection(proxy);
        } else {
            connection = url.openConnection(); // 프록시 없이 직접 연결
        }

        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }


    // 특정 멤버의 조직 이름 리스트 반환
    private List<String> findOrganizationNames(final Member member) {
        return memberOrganizationRepository.findOrganizationNamesByMember(member);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String GITHUB_API_BASE_URL = "https://api.github.com/repos";

    public RepositoryContentDto getRepositoryContents(long memberId, String repo, String branch) throws IOException {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(NoExistMemberException::new);

        // 개인 소유자로 먼저 시도
        String ownerName = member.getOriginName();
        RepositoryContentDto result = tryGetRepositoryContents(ownerName, repo, branch);

        // 개인 소유에서 찾지 못하면 조직 소유로 검색
        if (result == null) {
            List<String> organizationNames = findOrganizationNames(member);
            for (String orgName : organizationNames) {
                result = tryGetRepositoryContents(orgName, repo, branch);
                if (result != null) {
                    break;
                }
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("Could not find repository " + repo + " under any owner.");
        }

        return result;
    }

    // 특정 소유자(개인 또는 조직)에서 레포지토리를 가져오기 시도
    private RepositoryContentDto tryGetRepositoryContents(String ownerName, String repo, String branch) {
        try {
            // GitHub ZIP 다운로드 URL
            String zipUrl = String.format("https://github.com/%s/%s/archive/refs/heads/%s.zip", ownerName, repo, branch);

            // 1. ZIP 파일 다운로드
            File tempZipFile = File.createTempFile(repo, ".zip");
            downloadFile(zipUrl, tempZipFile);

            // 2. ZIP 파일 압축 해제
            File tempDir = new File(tempZipFile.getParent(), repo);
            unzip(tempZipFile, tempDir);

            // 3. 폴더 및 파일 구조 생성
            RepositoryContentDto root = parseFolder(tempDir);

            // 4. 임시 파일 삭제
            tempZipFile.delete();
            deleteFolder(tempDir);

            return root;
        } catch (IOException e) {
            System.err.println("Failed to retrieve repository for owner: " + ownerName);
            return null;
        }
    }

    // 파일 다운로드
    private void downloadFile(String url, File destinationFile) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    // ZIP 파일 압축 해제
    private void unzip(File zipFile, File destDir) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }

    // 폴더 및 파일 구조 생성
    private RepositoryContentDto parseFolder(File folder) throws IOException {
        RepositoryContentDto dto = new RepositoryContentDto();
        dto.setName(folder.getName());
        dto.setType("directory");
        dto.setChildren(new ArrayList<>());

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                dto.getChildren().add(parseFolder(file)); // 재귀적으로 하위 폴더 탐색
            } else {
                RepositoryContentDto fileDto = new RepositoryContentDto();
                fileDto.setName(file.getName());
                fileDto.setType("file");
                fileDto.setContent(readFileContent(file)); // 파일 내용 읽기
                dto.getChildren().add(fileDto);
            }
        }
        return dto;
    }

    // 파일 내용 읽기
    private String readFileContent(File file) throws IOException {
        if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg")) {
            // 바이너리 파일은 Base64로 인코딩
            byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileBytes);
        } else {
            // 텍스트 파일은 그대로 읽기
            StringBuilder content = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString();
        }
    }

    // 폴더 삭제
    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }
}
