package dododocs.dododocs.analyze.domain.repository;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepoAnalyzeRepository extends JpaRepository<RepoAnalyze, Long> {
    Optional<RepoAnalyze> findByRepositoryName(final String repositoryName);
    List<RepoAnalyze> findByMember(final Member member);
    void deleteById(final Long id);

    @Query("SELECT r FROM RepoAnalyze r WHERE r.member.originName = :originName AND r.repositoryName = :repositoryName AND r.branchName = :branchName")
    Optional<RepoAnalyze> findByMemberNameAndRepositoryNameAndBranchName(
            @Param("originName") final String originName,
            @Param("repositoryName") final String repositoryName,
            @Param("branchName") final String branchName
    );
}

