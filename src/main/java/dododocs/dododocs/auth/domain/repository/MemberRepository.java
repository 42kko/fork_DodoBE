package dododocs.dododocs.auth.domain.repository;

import dododocs.dododocs.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsBySocialLoginId(final Long socialLoginId);
    Member findBySocialLoginId(final Long socialLoginId);
    Member findByOriginName(final String originName);
}
