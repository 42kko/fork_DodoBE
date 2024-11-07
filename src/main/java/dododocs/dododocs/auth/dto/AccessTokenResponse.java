package dododocs.dododocs.auth.dto;

import lombok.Getter;

@Getter
public class AccessTokenResponse {
    private String accessToken;

    public AccessTokenResponse(final String accessToken) {
        this.accessToken = accessToken;
    }
}
