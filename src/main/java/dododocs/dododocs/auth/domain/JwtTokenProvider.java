package dododocs.dododocs.auth.domain;

import dododocs.dododocs.auth.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long accessTokenValidityInSeconds;

    public JwtTokenProvider(@Value("${security.jwt.token.secret_key}") final String secretKey,
                            @Value("${security.jwt.token.expire_length.access_token}") final long accessTokenValidityInSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
    }

    public String createAccessToken(final long memberId) {
        return createToken(String.valueOf(memberId), accessTokenValidityInSeconds);
    }

    public String createToken(final String payload, final long tokenValidityInSeconds) {
        final Date now = new Date();
        final Date expireDate = new Date(now.getTime() + tokenValidityInSeconds);

        return Jwts.builder()
                .setSubject(payload)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(final String token) {
        try {
            final Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("변조되었거나 만료된 토큰입니다.");
        }
    }

    public Long getMemberId(final String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }
}
