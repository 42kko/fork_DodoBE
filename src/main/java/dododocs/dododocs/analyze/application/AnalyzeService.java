package dododocs.dododocs.analyze.application;

import aj.org.objectweb.asm.TypeReference;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.domain.repository.MemberOrganizationRepository;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeRequest;
import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeResponse;
import dododocs.dododocs.analyze.dto.RepositoryContentDto;
import dododocs.dododocs.analyze.dto.UploadGitRepoContentToS3Request;
import dododocs.dododocs.analyze.exception.MaxSizeRepoRegiserException;
import dododocs.dododocs.analyze.exception.NoExistGitRepoException;
import dododocs.dododocs.analyze.infrastructure.ExternalAiZipAnalyzeClient;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
import dododocs.dododocs.auth.exception.NoExistMemberException;
import dododocs.dododocs.chatbot.application.ChatbotService;
import dododocs.dododocs.chatbot.infrastructure.ExternalChatbotClient;
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
    private static final Integer REPO_REGISTER_MAX_SIZE = 3;
    private final ExternalAiZipAnalyzeClient externalAiZipAnalyzeClient;
    private final MemberRepository memberRepository;
    private final AmazonS3Client amazonS3Client;
    private final MemberOrganizationRepository memberOrganizationRepository;
    private final RepoAnalyzeRepository repoAnalyzeRepository;

    // GitHub 레포지토리를 ZIP 파일로 가져와 S3에 업로드
    public void uploadGithubRepoToS3(final UploadGitRepoContentToS3Request uploadGitRepoContentToS3Request, final long memberId) {

        final String repoName = uploadGitRepoContentToS3Request.getRepositoryName();
        final String branchName = uploadGitRepoContentToS3Request.getBranchName();

        // Member를 조회
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(NoExistMemberException::new);

        final List<RepoAnalyze> repoAnalyzes = repoAnalyzeRepository.findByMember(member);

        if(repoAnalyzes.size() >= REPO_REGISTER_MAX_SIZE) {
            throw new MaxSizeRepoRegiserException("최대 3개의 레포지토리를 등록 가능합니다.");
        }

        // 개인 소유자로 먼저 시도
        String ownerName = member.getOriginName();

        // docs_key : msung99_moheng_main_DOCS.zip
        // readme_key : msung99_moheng_main_README.md
        String docsKey = ownerName + "_" + repoName + "_" + branchName + "_DOCS.zip";
        String readmeKey = ownerName + "_" + repoName + "_" + branchName + "_README.md";

        boolean success = tryUploadFromOwner(
                member.getAccessToken(),
                String.format("https://github.com/%s/%s/%s", ownerName, repoName, branchName),
                uploadGitRepoContentToS3Request.isIncludeTest(),
                docsKey,
                readmeKey,
                uploadGitRepoContentToS3Request.isKorean(),
                ownerName,
                repoName,
                branchName);

        // 개인 소유에서 찾지 못하면 조직 소유로 검색
        if (!success) {
            List<String> organizationNames = findOrganizationNames(member);
            for (String orgName : organizationNames) {
                success = tryUploadFromOwner(
                        member.getAccessToken(),
                        String.format("https://github.com/%s/%s/%s", ownerName, repoName, branchName),
                        uploadGitRepoContentToS3Request.isIncludeTest(),
                        docsKey,
                        readmeKey,
                        uploadGitRepoContentToS3Request.isKorean(),
                        orgName, repoName, branchName);
                if (success) {
                    break;
                }
            }
        }

        if(!success) {
            throw new NoExistGitRepoException("존재하지 않는 레포지토리 또는 브랜치입니다.");
        }

        String s3Key = ownerName + "-" + repoName;
        String repoUrl = String.format("https://github.com/%s/%s/%s", ownerName, repoName, branchName);

        repoAnalyzeRepository.save(
                new RepoAnalyze(repoName,
                        branchName,
                        readmeKey,
                        docsKey,
                        repoUrl,
                        member)
        );
    }

    private boolean tryUploadFromOwner(String accessToken, String repoUrl, boolean includeTest, String docsKey, String readmeKey, boolean korean,
                                       String ownerName, String repoName, String branchName) {

        String downloadUrl = String.format("https://api.github.com/repos/%s/%s/zipball/%s", ownerName, repoName, branchName);

        File tempFile = null;
        try {
            tempFile = File.createTempFile(repoName, ".zip");
            downloadFileFromUrlWithAuth(downloadUrl, accessToken, tempFile);
        } catch (Exception e) {
            return false;
        }

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.addUserMetadata("repo_url", repoUrl);
            metadata.addUserMetadata("include_test", String.valueOf(includeTest));
            metadata.addUserMetadata("docs_key", docsKey);
            metadata.addUserMetadata("readme_key", readmeKey);
            metadata.addUserMetadata("korean", String.valueOf(korean));

            amazonS3Client.putObject(new PutObjectRequest("haon-dododocs", "source/" + ownerName + "-" + repoName + "-" + branchName, tempFile).withMetadata(metadata));
        } catch (Exception e) {
            return false;
        }

        tempFile.delete();
        return true;
    }

    private void downloadFileFromUrlWithAuth(String url, String accessToken, File destinationFile) throws IOException {
        URLConnection connection = new URL(url).openConnection();

        System.out.println("🌴 accessToken: " + accessToken);

        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

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
