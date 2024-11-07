package dododocs.dododocs.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OAuthUriResponse {
    @JsonProperty("oauthUri")
    private String oAuthUri;

    private OAuthUriResponse() {
    }

    public OAuthUriResponse(final String oAuthUri) {
        this.oAuthUri = oAuthUri;
    }
}
