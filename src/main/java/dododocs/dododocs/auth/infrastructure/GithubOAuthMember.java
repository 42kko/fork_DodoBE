package dododocs.dododocs.auth.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GithubOAuthMember {
    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String nickName;

    @JsonProperty("login")
    private String originName;

    public GithubOAuthMember(final String email, final String nickName, final  String originName) {
        this.email = email;
        this.nickName = nickName;
        this.originName = originName;
    }
}
