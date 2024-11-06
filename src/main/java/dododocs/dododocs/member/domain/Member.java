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

    @Column(name = "name")
    private String name;

    protected Member() {
    }

    public Member(final String email, final String name) {
        this.email = email;
        this.name = name;
    }
}
