package com.cobuy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
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
                .requestMatchers(new AntPathRequestMatcher("/find")).permitAll()  // 개인 아이디 찾기
                .requestMatchers(new AntPathRequestMatcher("/findId")).permitAll()  // 아이디 찾기 완료
                .requestMatchers(new AntPathRequestMatcher("/findPw")).permitAll()  // 비밀번호 재설정
                .requestMatchers(new AntPathRequestMatcher("/updatePw")).permitAll()  // 비밀번호 변경 완료
                
                // 업체 회원 관련 페이지
                .requestMatchers(new AntPathRequestMatcher("/admin/join")).permitAll()  // 업체 회원가입
                .requestMatchers(new AntPathRequestMatcher("/admin/find")).permitAll()  // 업체 아이디 찾기
                
                // 셀러 회원 관련 페이지
                .requestMatchers(new AntPathRequestMatcher("/admin/seller/join")).permitAll()  // 셀러 회원가입
                .requestMatchers(new AntPathRequestMatcher("/admin/seller/find")).permitAll()  // 셀러 아이디 찾기
                
                // 아이디 중복체크 API 접근 허용
                .requestMatchers(new AntPathRequestMatcher("/admin/checkId")).permitAll()
                
                // 관리자 페이지 접근 제한 (로그인 필요)
                .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                
                // 위에서 설정한 경로 외의 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            // 로그인 설정
            .formLogin(form -> form
                .loginPage("/login")  // 커스텀 로그인 페이지 경로
                .defaultSuccessUrl("/")  // 로그인 성공 시 이동할 페이지
                .permitAll()  // 로그인 페이지는 모든 사용자 접근 가능
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
                    new AntPathRequestMatcher("/admin/checkId"),    // 아이디 중복체크 API
                    new AntPathRequestMatcher("/h2-console/**")     // H2 콘솔
                )
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 암호화를 위한 BCrypt 인코더 사용
        return new BCryptPasswordEncoder();
    }
}
