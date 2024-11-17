package dododocs.dododocs.analyze.domain.repository;

import dododocs.dododocs.analyze.domain.MemberOrganization;
import dododocs.dododocs.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberOrganizationRepository extends JpaRepository<MemberOrganization, Long> {
    @Query("SELECT mo.name FROM MemberOrganization mo WHERE mo.member = :member")
    List<String> findOrganizationNamesByMember(final Member member);
}
