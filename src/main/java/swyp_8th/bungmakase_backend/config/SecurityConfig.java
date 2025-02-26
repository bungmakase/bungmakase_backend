package swyp_8th.bungmakase_backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    @Autowired
//    private ClientRegistrationRepository clientRegistrationRepository;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 허용
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/oauth2/authorization/*").permitAll()
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
                .httpBasic(httpBasic -> httpBasic.disable())   // HTTP Basic 비활성화
                .oauth2Login(oauth2 -> oauth2.disable());


//                .oauth2Login(oauth2 -> oauth2
//                        .authorizationEndpoint(endpoint -> endpoint
//                                .authorizationRequestResolver(customAuthorizationRequestResolver(clientRegistrationRepository))
//                        )
//                );

        return http.build();
    }

//    @Bean
//    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
//        return new OAuth2AuthorizationRequestResolver() {
//            private final DefaultOAuth2AuthorizationRequestResolver defaultResolver =
//                    new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
//
//            @Override
//            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
//                return customizeRequest(defaultResolver.resolve(request));
//            }
//
//            @Override
//            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
//                return customizeRequest(defaultResolver.resolve(request, clientRegistrationId));
//            }
//
//            private OAuth2AuthorizationRequest customizeRequest(OAuth2AuthorizationRequest request) {
//                if (request == null) {
//                    return null;
//                }
//
//                // 기존 파라미터에 state=local 추가
//                Map<String, Object> additionalParameters = new HashMap<>(request.getAdditionalParameters());
//                additionalParameters.put("state", "local");
//
//                return OAuth2AuthorizationRequest.from(request)
//                        .additionalParameters(additionalParameters)
//                        .build();
//            }
//        };
//    }



}