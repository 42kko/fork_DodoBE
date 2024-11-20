package dododocs.dododocs.analyze.application;

import com.amazonaws.services.s3.AmazonS3Client;
import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.domain.repository.MemberOrganizationRepository;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeRequest;
import dododocs.dododocs.analyze.dto.ExternalAiZipAnalyzeResponse;
import dododocs.dododocs.analyze.infrastructure.ExternalAiZipAnalyzeClient;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
import dododocs.dododocs.auth.exception.NoExistMemberException;
import dododocs.dododocs.member.domain.Member;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLConnection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
@Service
public class AnalyzeService {
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
        try {
            String bucketDetailName = ownerName + "-" + repoName;
            String downloadUrl = String.format("https://github.com/%s/%s/archive/refs/heads/%s.zip", ownerName, repoName, branchName);
            System.out.println("Attempting to download from GitHub URL: " + downloadUrl);

            // ZIP 파일을 임시 디렉토리에 저장
            File tempFile = File.createTempFile(repoName, ".zip");
            downloadFileFromUrl(downloadUrl, tempFile);

            // S3에 업로드
            amazonS3Client.putObject("haon-dododocs", bucketDetailName, tempFile);

            // 업로드 후 임시 파일 삭제
            tempFile.delete();
            return true;
        } catch (IOException e) {
            // 레포지토리를 찾지 못하거나 다운로드 실패 시 false 반환
            System.err.println("Failed to upload repository from owner: " + ownerName);
            e.printStackTrace();
            return false;
        }
    }

    // URL에서 파일 다운로드
    private void downloadFileFromUrl(String downloadUrl, File destinationFile) throws IOException {
        URL url = new URL(downloadUrl);
        URLConnection connection = url.openConnection();
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
}
