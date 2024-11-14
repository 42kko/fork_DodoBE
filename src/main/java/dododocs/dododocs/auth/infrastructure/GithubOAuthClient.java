package dododocs.dododocs.auth.infrastructure;

import dododocs.dododocs.auth.exception.InvalidOAuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class GithubOAuthClient {
    private final RestTemplate restTemplate;
    private final String redirectUri;
    private final String clientId;
    private final String tokenUri;
    private final String userUri;
    private final String clientSecret;

    public GithubOAuthClient(final RestTemplate restTemplate,
                             @Value("${oauth.github.redirect_uri}") final String redirectUri,
                             @Value("${oauth.github.client_id}") final String clientId,
                             @Value("${oauth.github.token_uri}") final String tokenUri,
                             @Value("${oauth.github.user_uri}") final String userUri,
                             @Value("${oauth.github.client_secret}") final String clientSecret) {
        this.restTemplate = restTemplate;
        this.redirectUri = redirectUri;
        this.clientId = clientId;
        this.tokenUri = tokenUri;
        this.userUri = userUri;
        this.clientSecret = clientSecret;
    }

    public GithubOAuthMember getOAuthMember(final String code) {
        final String accessToken = requestGithubAccessToken(code);

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "token " + accessToken);
        final HttpEntity<MultiValueMap<String, String>> userInfoRequestEntity = new HttpEntity<>(httpHeaders);

        final ResponseEntity<GithubOAuthMember> githubOAuthMember = restTemplate.exchange(
                userUri,
                HttpMethod.GET,
                userInfoRequestEntity,
                GithubOAuthMember.class
        );

        if(githubOAuthMember.getStatusCode().is2xxSuccessful()) {
            return githubOAuthMember.getBody();
        }

        throw new InvalidOAuthException("깃허브 소셜 로그인 제공처 서버에 예기치 못한 문제가 발생했습니다.");
    }

    private String requestGithubAccessToken(final String code) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        final HttpHeaders httpHeaders = new HttpHeaders();

        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);

        final HttpEntity<MultiValueMap<String, String>> accessTokenRequestEntity = new HttpEntity<>(params, httpHeaders);

        final ResponseEntity<OAuthAccessToken> accessToken = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                accessTokenRequestEntity,
                OAuthAccessToken.class
        );

        return Optional.ofNullable(accessToken.getBody())
                .orElseThrow(RuntimeException::new)
                .getAccessToken();
    }
}
