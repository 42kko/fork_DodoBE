package dododocs.dododocs.auth.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GithubOAuthUriProvider {
    private final String authorizationUri;
    private final String clientId;

    public GithubOAuthUriProvider(@Value("${oauth.github.authorize_uri}") final String authorizationUri,
                              @Value("${oauth.github.client_id}") final String clientId) {
        this.authorizationUri = authorizationUri;
        this.clientId = clientId;
    }

    public String generateUri() {
        return authorizationUri + "?"
                + "client_id=" + clientId;
    }
}
