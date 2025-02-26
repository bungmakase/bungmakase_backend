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
            // "Bearer " 제거 후 순수한 JWT 값 추출
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY) // 🔥 SECRET_KEY 확인
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();


            return UUID.fromString(userId);
    }


}
