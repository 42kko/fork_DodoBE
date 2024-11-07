package dododocs.dododocs.auth.domain;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenCreator {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenCreator(final JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String createToken(final long memberId) {
        final String accessToken = jwtTokenProvider.createAccessToken(memberId);
        return accessToken;
    }


    public Long extractMemberId(final String accessToken) {
        jwtTokenProvider.validateToken(accessToken);
        return jwtTokenProvider.getMemberId(accessToken);
    }
}
