package com.cobuy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

// CustomUserDetailsService import 추가
import com.cobuy.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스는 인증 없이 접근 가능하도록 설정
                .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()

                // H2 콘솔 접근 허용
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()

                // 로그인, 회원가입 등 인증이 필요없는 페이지 설정
                .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()

                // 일반 회원 관련 페이지
                .requestMatchers(new AntPathRequestMatcher("/join")).permitAll()  // 회원가입 step01
                .requestMatchers(new AntPathRequestMatcher("/join02")).permitAll()  // 회원가입 완료
                .requestMatchers(new AntPathRequestMatcher("/member/join")).permitAll()  // 개인 회원가입
                .requestMatchers(new AntPathRequestMatcher("/member/checkId")).permitAll()  // 개인 아이디 중복체크
                .requestMatchers(new AntPathRequestMatcher("/find")).permitAll()  // 개인 아이디 찾기
                .requestMatchers(new AntPathRequestMatcher("/findId")).permitAll()  // 아이디 찾기 완료
                .requestMatchers(new AntPathRequestMatcher("/findPw")).permitAll()  // 비밀번호 재설정
                .requestMatchers(new AntPathRequestMatcher("/updatePw")).permitAll()  // 비밀번호 변경 완료

                // 업체 회원 관련 페이지
                .requestMatchers(new AntPathRequestMatcher("/admin/join")).permitAll()  // 업체 회원가입
                .requestMatchers(new AntPathRequestMatcher("/admin/find")).permitAll()  // 업체 아이디 찾기

                // 셀러 회원 관련 페이지
                .requestMatchers(new AntPathRequestMatcher("/seller/join")).permitAll()  // 셀러 회원가입
                .requestMatchers(new AntPathRequestMatcher("/seller/find")).permitAll()  // 셀러 아이디 찾기
                .requestMatchers(new AntPathRequestMatcher("/seller/checkId")).permitAll() // 셀러 아이디 중복체크

                // 아이디 중복체크 API 접근 허용
                .requestMatchers(new AntPathRequestMatcher("/admin/checkId")).permitAll()

                //상품페이지
                .requestMatchers(new AntPathRequestMatcher("/product")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/product/**")).permitAll()

                // 장바구니 관련 엔드포인트는 로그인한 사용자만 사용가능
                .requestMatchers(new AntPathRequestMatcher("/cart/**")).hasAnyRole("USER", "SELLER", "ADMIN")

                // 검색 API 접근 허용
                .requestMatchers(new AntPathRequestMatcher("/admin/search")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/seller/search")).permitAll()

                // 관리자 페이지 접근 제한 (로그인 필요)
                .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")

                //셀러 페이지 접근 제한(로그인 필요)
                .requestMatchers(new AntPathRequestMatcher("/seller/**")).hasRole("SELLER")

                //사용자 페이지 접근 제한(로그인 필요)
                //.requestMatchers(new AntPathRequestMatcher("/mypage/profile")).hasRole("USER")



                // 위에서 설정한 경로 외의 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            // 로그인 설정
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login-process")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    // 사용자의 권한에 따라 리다이렉트 URL 결정
                    String redirectUrl = "/product"; // 기본 URL (USER)

                    if (authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        redirectUrl = "/admin/order/status";
                    } else if (authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_SELLER"))) {
                        redirectUrl = "/seller/order/status";
                    }

                    // 리다이렉트 수행
                    response.sendRedirect(redirectUrl);
                })
                .failureHandler((request, response, exception) -> {
                    String errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
                    response.sendRedirect("/login?error=true&exception=" +
                        URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
                })
                .permitAll()
            )
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  // 로그아웃 URL
                .logoutSuccessUrl("/")  // 로그아웃 성공 시 이동할 페이지
                .invalidateHttpSession(true)  // 로그아웃 시 세션 무효화
            )
            // H2 콘솔 사용을 위한 설정
            .headers(headers -> headers.frameOptions().disable())
            // CSRF 보호 기능 설정
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/admin/checkId"),
                    new AntPathRequestMatcher("/seller/checkId"),
                    new AntPathRequestMatcher("/member/checkId"),
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/admin/search"),
                    new AntPathRequestMatcher("/seller/search"),// GET 검색 요청은 CSRF 검사 제외
                    new AntPathRequestMatcher("/manage/**") // manage 관련 API는 CSRF 검증 제외
                )
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
}
