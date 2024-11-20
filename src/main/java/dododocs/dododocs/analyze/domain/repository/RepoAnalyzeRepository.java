package dododocs.dododocs.analyze.domain.repository;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoAnalyzeRepository extends JpaRepository<RepoAnalyze, Long> {
    Optional<RepoAnalyze> findByRepositoryName(final String repositoryName);
}
