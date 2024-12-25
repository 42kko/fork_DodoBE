package dododocs.dododocs.auth.application;

import dododocs.dododocs.auth.dto.GithubOAuthMemberWithAccessToken;
import dododocs.dododocs.auth.exception.NoExistMemberException;
import dododocs.dododocs.auth.infrastructure.GithubOAuthClient;
import dododocs.dododocs.auth.infrastructure.GithubOAuthMember;
import dododocs.dododocs.auth.domain.GithubOAuthUriProvider;
import dododocs.dododocs.auth.domain.JwtTokenCreator;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
import dododocs.dododocs.auth.infrastructure.GithubOrganizationClient;
import dododocs.dododocs.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {
    private final GithubOAuthUriProvider githubOAuthUriProvider;
    private final JwtTokenCreator jwtTokenCreator;
    private final GithubOAuthClient githubOAuthClient;
    private final MemberRepository memberRepository;
    private final GithubOrganizationClient githubOrganizationClient;

    public String generateUri() {
        return githubOAuthUriProvider.generateUri();
    }

    @Transactional
    public String generateTokenWithCode(final String code) throws Exception {
        final GithubOAuthMemberWithAccessToken githubOAuthMemberWithAccessToken = githubOAuthClient.getOAuthMember(code);
        final GithubOAuthMember githubOAuthMember = githubOAuthMemberWithAccessToken.getGithubOAuthMember();
        final String githubAccessToken = githubOAuthMemberWithAccessToken.getAccessToken();

        final Member foundMember = findOrCreateMember(githubOAuthMemberWithAccessToken.getGithubOAuthMember(), githubAccessToken);

        githubOrganizationClient.saveMemberOrganizationNames(foundMember, githubOAuthMember.getOriginName());
        final String accessToken = jwtTokenCreator.createToken(foundMember.getId());
        return accessToken;
    }

    public Member findOrCreateMember(final GithubOAuthMember githubOAuthmember, final String githubAccessToken) {
        final Long socialLoginId = githubOAuthmember.getSocialLoginId();

        if(!memberRepository.existsBySocialLoginId(socialLoginId)) {
            memberRepository.save(generateMember(githubOAuthmember, githubAccessToken));
        }
        final Member foundMember = memberRepository.findBySocialLoginId(socialLoginId);
        return foundMember;
    }

    private Member generateMember(final GithubOAuthMember githubOAuthMember, final String githubAccessToken) {
        return new Member(githubOAuthMember.getSocialLoginId(), githubOAuthMember.getNickName(), githubOAuthMember.getOriginName(), githubAccessToken);
    }

    public Long extractMemberId(final String accessToken) {
        final Long memberId = jwtTokenCreator.extractMemberId(accessToken);

        if(!memberRepository.existsById(memberId)) {
            throw new NoExistMemberException("존재하지 않는 멤버입니다.");
        }
        return memberId;
    }
}
