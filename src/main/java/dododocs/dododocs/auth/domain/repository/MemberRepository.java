package dododocs.dododocs.auth.domain.repository;

import dododocs.dododocs.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Member findByEmail(String email);
}
