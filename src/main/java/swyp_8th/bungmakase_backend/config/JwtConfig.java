package swyp_8th.bungmakase_backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import swyp_8th.bungmakase_backend.exception.InvalidTokenException;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtConfig {
    private byte[] SECRET_KEY;

    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 30; // 1달

    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKey){
        this.SECRET_KEY = Base64.getDecoder().decode(secretKey);
    }

    public String generateToken(UUID userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // 토큰 검증 및 유저 ID 추출
    public UUID getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            return UUID.fromString(claims.getSubject());
        } catch (SignatureException | IllegalArgumentException e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }


}
