package dododocs.dododocs.auth.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GithubOAuthMember {
    @JsonProperty("id")
    private Long socialLoginId;

    @JsonProperty("name")
    private String nickName;

    @JsonProperty("login")
    private String originName;

    public GithubOAuthMember(final Long socialLoginId, final String nickName, final  String originName) {
        this.socialLoginId = socialLoginId;
        this.nickName = nickName;
        this.originName = originName;
    }
}
