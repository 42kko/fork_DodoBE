package dododocs.dododocs.analyze.domain;

import dododocs.dododocs.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "member_organization")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MemberOrganization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public MemberOrganization(final Member member, final String name) {
        this.member = member;
        this.name = name;
    }
}
