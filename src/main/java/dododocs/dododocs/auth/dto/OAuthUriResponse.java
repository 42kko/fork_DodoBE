package dododocs.dododocs.auth.dto;

import lombok.Getter;

@Getter
public class OAuthUriResponse {
    private String oAuthUri;

    private OAuthUriResponse() {
    }

    public OAuthUriResponse(final String oAuthUri) {
        this.oAuthUri = oAuthUri;
    }
}
