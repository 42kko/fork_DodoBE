package dododocs.dododocs.auth.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GithubOAuthMember {
    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    public GithubOAuthMember(final String email, final String name) {
        this.email = email;
        this.name = name;
    }
}
