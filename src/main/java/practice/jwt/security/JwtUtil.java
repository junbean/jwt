package practice.jwt.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import practice.jwt.entity.User;

import java.util.Date;


// JWT토큰을 만들고 해석하고 검증하는 도구
@Component
public class JwtUtil {
    // 비밀키 
    // JWT를 만들때 이 키로 서명을 한다, JWT를 해킹하려면 이 키를 알아야 한다
    private final String SECRET_KEY = "secret";

    // JWT 토큰 생성
    public String generateToken(User user) {
        return Jwts.builder()                       // JWT 만들기 시작
                .setSubject(user.getEmail())        // 톤큰에 사용자 이메일 저장
                .claim("role", user.getRole())   // 역할 정보 추가
                .setIssuedAt(new Date())            // 토큰 발급 시간 저장
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 토큰 만료 시간 설정 (현재 시간 + 1시간)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)                     // HS256 방식 비밀키를 이용해 서명
                .compact();                     // JWT 문자열 변환 (최종 결과)
    }

    // 토큰에서 이메일 추출하기
    // JWT 안에 저장된 이메일을 꺼냄
    public String extractEmail(String token) {
        return Jwts
                .parser()                   // 토큰을 해석하는 도구
                .setSigningKey(SECRET_KEY)  // 이 키로 서명된 토큰만 해석 가능
                .parseClaimsJws(token)      // 토큰을 해석해서 데이터 가져오기
                .getBody().getSubject();    // 저장된 이메일 추출
    }

    // 토큰에서 사용자의 역할을 가져옴
    public String extractRole(String token) {
        return Jwts
                .parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody().get("role", String.class);
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            // 토큰이 유효하면 그대로 실행됨
            // 만약 서명이 다르거나, 만료된 토큰이면 오류 발생
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);  
            return true;
        } catch (Exception e) {
            // 오류 발생하면 false 반환
            return false;
        }
    }
}
