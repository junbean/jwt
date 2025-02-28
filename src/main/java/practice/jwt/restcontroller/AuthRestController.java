package practice.jwt.restcontroller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import practice.jwt.entity.User;
import practice.jwt.repository.UserRepository;
import practice.jwt.security.JwtUtil;


// 사용자의 로그인/로그아웃 기능 제공
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    /*
    @Autowired
    public AuthRestController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }
    */

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String password,
            HttpServletResponse response
    ) {
        // 사용자의 이메일과 비밀번호가 맞는지 확인
        // 만약 틀리면 오류 발생! (자동으로 예외 던져짐)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);   // Spring Security가 "이 사람 로그인했어!" 라고 기억함
        User user = userRepository.findByEmail(email).orElseThrow();            // 데이터베이스에서 이메일로 사용자 찾기, 만약 사용자가없다면 오류 발생
        String token = jwtUtil.generateToken(user);     // 로그인된 사용자 정보로 JWT 토큰 생성

        // 🔹 JWT를 HttpOnly 쿠키에 저장
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);   // 자바스크립트에서 접근 못하게 막음
        cookie.setSecure(true);     // HTTPS에서만 사용 (개발 시 false로 변경)
        cookie.setPath("/");        // 사이트 어디서든 사용 가능
        cookie.setMaxAge(60 * 60); // 1시간 동안 유지

        response.addCookie(cookie); //  클라이언트에게 쿠키를 응답으로 보냄!

        return ResponseEntity.ok("로그인 성공!");    // 클라이언트에게 성공 메세지 보냄

        //return ResponseEntity.ok(new JwtResponse(token));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // JWT 쿠키 삭제
        Cookie cookie = new Cookie("JWT", null);        // JWT쿠키를 만들고 값은 null로 지정
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok("로그아웃 성공!");
    }
}
