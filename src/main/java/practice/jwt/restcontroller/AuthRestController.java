package practice.jwt.restcontroller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import practice.jwt.dto.JwtResponse;
import practice.jwt.entity.User;
import practice.jwt.repository.UserRepository;
import practice.jwt.security.JwtUtil;

import java.security.Security;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public AuthRestController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam(name="email") String email,
            @RequestParam(name="password") String password,
            HttpServletResponse response
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(email).orElseThrow();
        String token = jwtUtil.generateToken(user);

        // 🔹 JWT를 HttpOnly 쿠키에 저장
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 사용 (개발 시 false로 변경)
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1시간

        response.addCookie(cookie);

        return ResponseEntity.ok("로그인 성공!");

        //return ResponseEntity.ok(new JwtResponse(token));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // JWT 쿠키 삭제
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok("로그아웃 성공!");
    }
}