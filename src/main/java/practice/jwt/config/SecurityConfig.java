package practice.jwt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import practice.jwt.security.JwtFilter;

@Configuration          // Spring 설정 파일을 나타냄
@EnableWebSecurity      // spring security 활성화
@RequiredArgsConstructor    // 필요한 생성자 자동 생성
public class SecurityConfig {
    // JWT 필터 : JWT 토큰을 검사하는 필터
    private final JwtFilter jwtFilter;

    /*
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
    */

    // AuthenticationManager → 로그인할 때 인증을 담당하는 객체
    // Spring Security에서 로그인 시 이메일 + 비밀번호 검증하는 역할
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    // BCryptPasswordEncoder() → 비밀번호를 BCrypt 방식으로 암호화 (해커가 원본 비번을 알아내지 못하도록 암호화)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 모든 보안 설정이 들어있는 핵심 메서드
    // SecurityFilterChain → HTTP 요청을 어떻게 보안할지 설정하는 객체
    // HttpSecurity → Spring Security에서 보안 설정을 구성하는 클래스]
    // 이 Security Filter Chain은 spring security가 자동으로 사용한다
    @Bean
    public SecurityFilterChain securityFilterchain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // csrf 보안 기능 비활성화
                .authorizeHttpRequests(auth -> auth     // 요청 권한 설정
                        .requestMatchers("/", "/signup", "/login", "/api/auth/**").permitAll() // 누구나 접근 가능
                        .requestMatchers("/dashboard").hasAnyRole("USER", "ADMIN")  // "USER" 또는 "ADMIN" 역할만 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // "ADMIN" 역할만 접근 가능
                        .anyRequest().authenticated()   // 나머지는 로그인해야 접근 가능
                )
                .formLogin(form -> form
                        .loginPage("/login")                // 로그인 페이지 지정
                        .defaultSuccessUrl("/dashboard")    // 로그인 성공 후 이동할 페이지
                        .permitAll()                         // 로그인 페이지는 누구나 접근 가능
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")               // 로그아웃할 때 요청할 URL
                        .logoutSuccessUrl("/")              // 로그아웃 성공 후 이동할 페이지
                        .invalidateHttpSession(true)        // 세션 삭제 (로그아웃 후 새로운 세션 필요)
                        .deleteCookies("JSESSIONID")    // JSESSIONID 쿠키 삭제 (자동 로그인 방지)
                        .permitAll()                        // 누구나 로그아웃 가능
                )
                // jwtFilter를 Spring Security의 UsernamePasswordAuthenticationFilter 앞에 추가
                // 즉, 로그인 검증 전에 JWT 토큰을 검사하도록 설정
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

/*
CSRF
    웹사이트는 사용자가 로그인할 때 브라우저에 세션 쿠키를 저장한다
    이 쿠키는 사용자가 그 사이트에 요청을 보낼 때마다 자동으로 포함

    CSRF 공격은 이 점을 악용한다
    공격자가 사용자가 이미 로그인한 사이트로, 사용자가 의도하지 않은 요청을 보내도록 속이는 것

    예시


*/


/*
@Bean
public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
            .and().build();
}
*/