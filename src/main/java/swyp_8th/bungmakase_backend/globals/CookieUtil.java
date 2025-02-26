package swyp_8th.bungmakase_backend.globals;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public ResponseCookie createCookie(String jwtToken, String cookieDomain) {
        int maxAge = 60 * 60 * 24 * 30; // 30일

        return ResponseCookie.from("token", jwtToken)
                .domain(cookieDomain)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(false)
                .secure(true)
                .sameSite("None")
                .build();
    }
}
