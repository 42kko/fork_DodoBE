package dododocs.dododocs.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Table(name = "member")
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "social_login_id")
    private Long socialLoginId;

    @Column(name = "nick_name")
    private String nickname;

    @Column(name = "origin_name")
    private String originName;

    @Column(name = "access_token")
    private String accessToken;


    protected Member() {
    }

    public Member(final Long socialLoginId, final String nickname, final String originName, final String accessToken) {
        this.socialLoginId = socialLoginId;
        this.nickname = nickname;
        this.originName = originName;
        this.accessToken = accessToken;
    }


    public Member(final Long socialLoginId, final String nickname, final String originName) {
        this.socialLoginId = socialLoginId;
        this.nickname = nickname;
        this.originName = originName;
    }

    public Member(final String email) {
        this.socialLoginId = 123123123L;
        this.nickname = "devhaon";
        this.originName = "lee min sung";
    }
}
