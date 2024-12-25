package dododocs.dododocs.auth.dto;

import dododocs.dododocs.auth.infrastructure.GithubOAuthMember;
import lombok.Getter;

@Getter
public class GithubOAuthMemberWithAccessToken {
    private GithubOAuthMember githubOAuthMember;
    private String accessToken;

    public GithubOAuthMemberWithAccessToken(final GithubOAuthMember githubOAuthMember, final String accessToken) {
        this.githubOAuthMember = githubOAuthMember;
        this.accessToken = accessToken;
    }
}
