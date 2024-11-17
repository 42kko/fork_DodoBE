package dododocs.dododocs.analyze.dto;

import lombok.Getter;

@Getter
public class UploadGitRepoContentToS3Request {
    private String repositoryName;
    private String branchName;
}
