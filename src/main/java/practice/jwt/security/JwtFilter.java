package practice.jwt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // JWT 쿠키에서 토큰 추출
        Cookie[] cookies = request.getCookies();
        String token = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

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