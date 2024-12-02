package dododocs.dododocs.analyze.domain.repository;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepoAnalyzeRepository extends JpaRepository<RepoAnalyze, Long> {
    Optional<RepoAnalyze> findByRepositoryName(final String repositoryName);
    List<RepoAnalyze> findByMember(final Member member);
    void deleteById(final Long id);
}

