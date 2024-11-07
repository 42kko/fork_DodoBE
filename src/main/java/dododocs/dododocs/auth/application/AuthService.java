package dododocs.dododocs.auth.application;

import dododocs.dododocs.auth.exception.NoExistMemberException;
import dododocs.dododocs.auth.infrastructure.GithubOAuthClient;
import dododocs.dododocs.auth.infrastructure.GithubOAuthMember;
import dododocs.dododocs.auth.domain.GithubOAuthUriProvider;
import dododocs.dododocs.auth.domain.JwtTokenCreator;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
import dododocs.dododocs.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class AuthService {
    private final GithubOAuthUriProvider githubOAuthUriProvider;
    private final JwtTokenCreator jwtTokenCreator;
    private final GithubOAuthClient githubOAuthClient;
    private final MemberRepository memberRepository;

    public AuthService(final GithubOAuthUriProvider githubOAuthUriProvider,
                       final JwtTokenCreator jwtTokenCreator,
                       final GithubOAuthClient githubOAuthClient,
                       final MemberRepository memberRepository) {
        this.githubOAuthUriProvider = githubOAuthUriProvider;
        this.jwtTokenCreator = jwtTokenCreator;
        this.githubOAuthClient = githubOAuthClient;
        this.memberRepository = memberRepository;
    }

    public String generateUri() {
        return githubOAuthUriProvider.generateUri();
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
        return new Member(githubOAuthMember.getEmail(), githubOAuthMember.getNickName(), githubOAuthMember.getOriginName());
    }

    public Long extractMemberId(final String accessToken) {
        final Long memberId = jwtTokenCreator.extractMemberId(accessToken);

        if(!memberRepository.existsById(memberId)) {
            throw new NoExistMemberException("존재하지 않는 멤버입니다.");
        }
        return memberId;
    }
}
