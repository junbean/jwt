package practice.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import practice.jwt.repository.UserRepository;
import practice.jwt.security.JwtFilter;
import practice.jwt.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SecurityFilterChain → HTTP 요청을 어떻게 보안할지 설정하는 객체
    // HttpSecurity http → Spring Security에서 보안 설정을 구성하는 클래스
    @Bean
    public SecurityFilterChain securityFilterchain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // csrf 보안 기능 비활성화
                .authorizeHttpRequests(auth -> auth     // 요청 권한 설정
                        .requestMatchers("/", "/signup", "/login", "/api/auth/**").permitAll() // .requestMatchers("경로") : 특정 URL에 대한 접근 권한 설정, permitAll() : 누구나 접근 가능
                        .requestMatchers("/dashboard").hasRole("USER")  // 사용자 접근 가능
                        .requestMatchers("/admin").hasRole("ADMIN")  // 관리자만 접근 가능
                        .anyRequest().authenticated()   // 위에서 허용한 것 제외한 모든 요청은 로그인 필요
                )
                .formLogin(form -> form
                        .loginPage("/login")                // 사용자가 로그인할 페이지 지정
                        .defaultSuccessUrl("/dashboard")    // 로그인 성공 후 이동할 페이지
                        .permitAll()                        // 로그인 페이지는 누구나 접근 가능(로그인 폼 제출을 허용하는 의미)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")               // 로그아웃을 수행할 URL
                        .logoutSuccessUrl("/")              // 로그아웃 성공 후 이동할 페이지
                        .invalidateHttpSession(true)        // 세션 삭제 (로그아웃 후 새로운 세션 필요)
                        .deleteCookies("JSESSIONID")    // JSESSIONID 쿠키 삭제 (자동 로그인 방지)
                        .permitAll()                        // 누구나 로그아웃할 수 있음 (로그인 안 해도 /logout 가능)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}



/*
@Bean
public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
            .and().build();
}
*/