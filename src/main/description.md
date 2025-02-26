# 실행 동작 과정


## 회원가입 과정
1. 사용자가 `/signup` 페이지에 접속
   - URL: GET /signup
   - 실행 코드: AuthController.signupForm()
2. 사용자가 이메일 & 비밀번호 입력 후 가입 버튼 클릭
   - URL: POST /signup
   - 실행 코드: AuthController.signup()
3. 비밀번호 암호화 & DB 저장
   - DB에 중복되는 이메일이 있는지 검사
   - BCrypt로 비밀번호 암호화
   - DB에 저장

<br>

## 로그인 과정 (JWT 발급)
1. 사용자가 `/login`페이지에 접속
   - URL: GET /login
   - 실행 코드: HomeController.loginForm()
2. 사용자가 로그인 폼에서 이메일 & 비밀번호 입력 후 제출
    - URL: POST /api/auth/login
    - 실행 코드: AuthRestController.login()
    - 로그인 진행 흐름
      1. 이메일 & 비밀번호를 입력한 후 POST /api/auth/login 요청
      2. AuthenticationManager가 DB에서 사용자를 찾고 비밀번호 검증
      3. 검증 성공 시 JwtUtil.generateToken(user)를 호출하여 JWT 발급
      4. 클라이언트가 JWT를 로컬스토리지 or 세션스토리지에 저장
      5. 로그인 완료 후, JWT를 사용하여 보호된 페이지(/dashboard)에 접근 가능
3. 