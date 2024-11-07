package dododocs.dododocs.member.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "member")
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "nick_name")
    private String nickname;

    @Column(name = "origin_name")
    private String originName;

    protected Member() {
    }

    public Member(final String email, final String nickname, final String originName) {
        this.email = email;
        this.nickname = nickname;
        this.originName = originName;
    }

    public Member(final String email) {
        this.email = email;
    }
}
