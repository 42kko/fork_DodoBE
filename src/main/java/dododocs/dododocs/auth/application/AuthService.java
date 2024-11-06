package dododocs.dododocs.auth.application;

import dododocs.dododocs.auth.exception.NoExistMemberException;
import dododocs.dododocs.auth.infrastructure.GithubOAuthClient;
import dododocs.dododocs.auth.infrastructure.GithubOAuthMember;
import dododocs.dododocs.auth.domain.GithubOAuthUriProvider;
import dododocs.dododocs.auth.domain.JwtTokenCreator;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
import dododocs.dododocs.member.domain.Member;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// https://park-algorithm.tistory.com/entry/Github-OAuth-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0#google_vignette
@Transactional(readOnly = true)
@Service
public class AuthService {
    private final GithubOAuthUriProvider oAuthUriProvider;
    private final JwtTokenCreator jwtTokenCreator;
    private final GithubOAuthClient githubOAuthClient;
    private final MemberRepository memberRepository;
    private final MeterFilter metricsHttpServerUriTagFilter;

    public AuthService(final GithubOAuthUriProvider oAuthUriProvider,
                       final JwtTokenCreator jwtTokenCreator,
                       final GithubOAuthClient githubOAuthClient,
                       final MemberRepository memberRepository, @Qualifier("metricsHttpServerUriTagFilter") MeterFilter metricsHttpServerUriTagFilter) {
        this.oAuthUriProvider = oAuthUriProvider;
        this.jwtTokenCreator = jwtTokenCreator;
        this.githubOAuthClient = githubOAuthClient;
        this.memberRepository = memberRepository;
        this.metricsHttpServerUriTagFilter = metricsHttpServerUriTagFilter;
    }

    public String generateUri() {
        return oAuthUriProvider.generateUri();
    }

    public String generateTokenWithCode(final String code) {
        final GithubOAuthMember githubOAuthMember = githubOAuthClient.getOAuthMember(code);
        final Member foundMember = findOrCreateMember(githubOAuthMember);
        final String accessToken = jwtTokenCreator.createToken(foundMember.getId());
        return accessToken;
    }

    public Member findOrCreateMember(final GithubOAuthMember githubOAuthmember) {
        final String email = githubOAuthmember.getEmail();

        if(!memberRepository.existsByEmail(email)) {
            memberRepository.save(generateMember(githubOAuthmember));
        }
        final Member foundMember = memberRepository.findByEmail(email);
        return foundMember;
    }

    private Member generateMember(final GithubOAuthMember githubOAuthMember) {
        return new Member(githubOAuthMember.getEmail(), githubOAuthMember.getName());
    }

    public Long extractMemberId(final String accessToken) {
        final Long memberId = jwtTokenCreator.extractMemberId(accessToken);

        if(!memberRepository.existsById(memberId)) {
            throw new NoExistMemberException("존재하지 않는 멤버입니다.");
        }
        return memberId;
    }
}
