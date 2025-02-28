package practice.jwt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// 모든 요청을 검사해서, JWT 토큰이 있으면 자동으로 로그인된 사용자로 인식
// 로그인하면 자동으로 요청마다 JWT를 확인해서 사용자 정보를 저장
// 매번 로그인할 필요 없이, JWT 쿠키만 있으면 로그인 상태 유지 가능
// 쉽게 말해서 로그인한 사용자가 맞는지 자동으로 확인해주는 보안 시스템
@Component
@RequiredArgsConstructor
// OncePerRequestFilter : 모든 요청(request)에 한 번씩 실행되는 필터, 든 요청에서 JWT 검사를 자동으로 함
public class JwtFilter extends OncePerRequestFilter {
    // JWT 토큰을 검사하고 분석하는 도구(유틸리티 클래스)
    private final JwtUtil jwtUtil;

    /*
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    */

    /*
    사용자가 요청을 보낼때 사용됨
        HttpServletRequest : 사용자가 보낸 요청 정보
        HttpServletResponse : 서버가 보낼 응답 정보
        FilterChain : 다음 필터로 넘길 때 필요
    */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();    // 사용자가 보낸 쿠키 가져오기
        String token = null;    // 토큰

        // 쿠키가 있는지 확인
        if (cookies != null) {  
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {   // "JWT"라는 이름의 쿠키 찾기
                    token = cookie.getValue();          // 찾았다면 그 쿠키 값(토큰) 저장
                }
            }
        }

        // JWT 토큰이 있으면 검사 + 사용자 인증
        // 토큰이 있고, 유효한 토큰인지 검사
        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);         // 토큰에서 이메일(email) 정보 추출
            String role = jwtUtil.extractRole(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());  // Spring Security에서 "이 사람 인증됨!" 하고 저장하는 객체
            SecurityContextHolder.getContext().setAuthentication(authentication);   // 로그인된 사용자 정보 저장

            // 역할 정보를 ThymeLeaf에서 사용할 수 있도록 request 속성으로 저장
            request.setAttribute("userEmail", email);
            request.setAttribute("userRole", role);
        }

        // 필터는 여러 개 있을 수 있음
        // 다음 필터로 요청(request)을 넘김, 즉 JWT 검사 킅났으면 원래 요청하던 API로 가게한다
        filterChain.doFilter(request, response);
    }
}



/*

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = request.getHeader("Authorization");

    if(token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);
        if(jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
    filterChain.doFilter(request, response);
}

*/



/*

// JWT 토큰이 있으면 검사 + 사용자 인증
// 토큰이 있고, 유효한 토큰인지 검사
if (token != null && jwtUtil.validateToken(token)) {
    String email = jwtUtil.extractEmail(token);         // 토큰에서 이메일(email) 정보 추출
    UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());  // Spring Security에서 "이 사람 인증됨!" 하고 저장하는 객체
    SecurityContextHolder.getContext().setAuthentication(authentication);   // 로그인된 사용자 정보 저장
}

// 필터는 여러 개 있을 수 있음
// 다음 필터로 요청(request)을 넘김, 즉 JWT 검사 킅났으면 원래 요청하던 API로 가게한다
filterChain.doFilter(request, response);

*/