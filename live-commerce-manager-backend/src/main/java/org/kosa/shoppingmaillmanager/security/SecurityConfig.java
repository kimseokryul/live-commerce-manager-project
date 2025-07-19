package org.kosa.shoppingmaillmanager.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

	@Autowired
	private JwtFilter jwtFilter;


	
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @SuppressWarnings("removal") // 특정 API의 사용 중단 경고(since 17+) 무시 — 보통 HttpSecurity 관련 lambda 표현식에서 발생
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // CORS 설정을 적용. 아래에 정의된 corsConfigurationSource()를 사용함
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // CSRF 보호 비활성화 (API 서버인 경우 보통 필요 없음)
            .csrf(csrf -> csrf.disable())

            // 기본 로그아웃 기능 비활성화 (우리는 자체 JWT 인증을 쓰기 때문에 Spring Security 기본 로그아웃 사용 안 함)
            .logout(logout -> logout.disable())

            // 요청 권한 설정
            .authorizeHttpRequests(auth -> auth

                // 아래 경로들은 인증이 필요한 요청들 (JWT 토큰이 있어야 접근 가능)
                .requestMatchers(
                    "/members/me",        // 내 정보 조회
                    "/products",          // 상품 목록
                    "/products/**",       // 상품 상세, 수정 등
                    "/dashboard/**",      // 관리자/대시보드 관련
                    "/video/upload",      // 영상 업로드
                    "/order/**",          // 주문 관련
                    "/broadcast/**"       // 방송 관련
                ).authenticated()

                // 그 외의 모든 요청은 인증 없이 허용
                .anyRequest().permitAll()
            )

            // UsernamePasswordAuthenticationFilter보다 먼저 우리가 만든 jwtFilter를 실행
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            // 필터 체인 구성 완료
            .build();
    }


    // 👇 CORS 설정 Bean 따로 등록
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOriginPatterns(List.of("http://localhost:*")); // Vue 서버주소 유연하게 허용
        config.setAllowedOriginPatterns(List.of("http://*")); // Vue 서버주소 유연하게 허용
        config.setAllowCredentials(true);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

