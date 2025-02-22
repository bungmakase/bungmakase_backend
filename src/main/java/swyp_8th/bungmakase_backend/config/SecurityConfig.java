package swyp_8th.bungmakase_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 회원가입, 로그인 API는 인증 없이 허용
                        .requestMatchers("/api/**").permitAll()
                        // Swagger 허용 URL
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()                // 나머지 요청은 인증 필요
                )
                .csrf(csrf -> csrf.disable())                    // CSRF 비활성화
                .formLogin(form -> form.disable())               // Form 로그인 비활성화
                .httpBasic(httpBasic -> httpBasic.disable());    // HTTP Basic 비활성화

        return http.build();
    }

}