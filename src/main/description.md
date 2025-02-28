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

<br>
<hr>
<br>

# 각 파일 역할 기능 정리

## Controller 계층

1. AuthController
    - GetMapping `/login`
        - 로그인 폼 제출
    - GetMapping `/signup`
        - 회원가입 폼 제출
    - PostMapping `/signup`
        - 폼으로부터 `email`, `password`를 받음
        - `userService.registerUser()`를 통해서 회우너 정보를 DB에 저장
        - 가입이 성공하면 `/login`으로 이동
        - 오류 발생시 `/signup`페이지에서 오류 메세지 출력
2. AuthRestController
    - 로그인/로그아웃 기능을 처리하는 컨트롤러
    - 사용자가 로그인을 하면 서버가 확인하고 **JWT토큰**을 발급해서 쿠키에 저장
    - `/api/auth/~`로 오는 요청을 처리함
    - `/login` - 로그인 기능
        - `/api/auth/login`주소로 **POST 요청**이 오면 실행됨
        - `email`, `password`를 받아옴
        - `HTTPServletResponse`를 받는데, 이건 클라이언트에게 응답을 보낼 때 사용
3. HomeController
    - 기본적인 폼 출력 컨트롤러
    - 사용자가 `/`, `/dashboard`, `/admin` 경로로 이동시 해당 폼을 보여줌

## model 계층

## dto 계층

## config 계층

<br>
<br>
<br>

# 배운거

### SecurityContextHolder

1. SecurityContextHolder 하는 일
    - 로그인한 사용자의 정보를 저장하고, 요청마다 로그인 상태를 유지하는 역할을 함
    - 즉, 스프링이 "이 사람 지금 로그인했어?"를 확인할 수 있도록 인증 정보를 관리하는 저장소라고 보면 됨

2. SecurityContextHolder를 쓰지 않는다면?
    - 로그인 상태를 유지하는 게 어려움
        - 로그인을 하면, 서버는 "이 사람이 로그인한 사람인지" 기억하고 있어야 함.
        - 하지만 서버는 한 번 요청이 끝나면 그 요청과 관련된 모든 정보를 잊어버림.
        - 그래서 로그인 상태를 유지하려면 뭔가 저장해둘 공간이 필요함
        - 로그인한 사용자의 정보를 쉽게 가져올 수 없음
            - 예를 들어, 게시판에서 "내가 작성한 글만 보기" 기능을 만든다고 해보자.
                1. 로그인한 사용자의 ID를 알아야 DB에서 해당 사용자가 쓴 글만 가져올 수 있음.
                2. 그런데 SecurityContextHolder가 없으면, 로그인한 사용자의 정보를 매번 요청에서 직접 꺼내야 함.
                3. 하지만 SecurityContextHolder를 쓰면, 어디서든 쉽게 로그인한 사용자 정보를 가져올 수 있음!
        - 즉, SecurityContextHolder가 없으면 로그인 상태를 유지하기 힘들고, 사용자 정보를 쉽게 가져오기 어려움


3. SecurityContextHolder가 제공하는 기능
    - SecurityContextHolder는 크게 3가지 기능을 제공함
        1. 로그인한 사용자의 정보를 저장할 수 있음
           ```
           SecurityContextHolder.getContext().setAuthentication(authentication);
           ```
            - `setAuthentication(authentication)` → 로그인한 사용자의 정보를 저장
            - 이렇게 하면 로그인한 상태를 유지할 수 있음
            - 이후 요청에서 SecurityContextHolder를 보면, 로그인한 사용자 정보를 알 수 있음
        2. 어디서든 로그인한 사용자의 정보를 쉽게 가져올 수 있음
           ```
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           String username = authentication.getName();
           System.out.println("로그인한 사용자: " + username);
           ```
            - `getAuthentication()` → 로그인한 사용자의 정보를 가져옴.
            - `.getName()` → 로그인한 사용자의 이메일(또는 ID) 을 가져올 수 있음.
            - 이걸로 "이 사용자가 누구인지?"를 쉽게 알 수 있음!
        3. 로그아웃하면 인증 정보를 삭제할 수 있음
           ```
           SecurityContextHolder.clearContext(); // 로그인 정보 삭제
           ```
            - `clearContext();` → 로그인 정보를 완전히 삭제함!
            - 이후 요청에서 `SecurityContextHolder.getContext().getAuthentication()` 하면 null이 나옴 → 즉, 로그아웃된 상태!

### 간혹가다 URL에 `http://localhost:8080/dashboard?continue` 붙는 경우

- 이 경우는 로그인 전에 접근했던 페이지를 기억한 것이다
- Spring Security는 사용자가 로그인하기 전에 원래 가려고 했던 페이지를 자동으로 저장함
- 그리고 로그인 후에는 그 페이지로 다시 이동하도록 설정하는 기능을 가지고 있음
- 쉽게 말하자면 `로그인을 하면 로그인 전 페이지로 자동으로 이동했다는 의미임`

<br>

### userDetailsService

1. `UserDetailsService` 뭐하는 놈인가?
    - Spring Security에서 사용자의 인증(로그인)을 처리하는 핵심 인터페이스
    - 로그인 요청이 오면 `loadUserByUsername()`을 실행해서 사용자를 찾음.
    - 찾은 사용자를 `UserDetails` 객체로 변환해서 반환함.
    - Spring Security가 `UserDetails`를 이용해서 로그인 검증을 수행함
    - 간략하자면 Spring Security는 UserDetailsService를 통해서 로그인을 처리 -> 찾은 사용자 UserDetails 객체 변환 및 반환

2. UserDetailsService의 핵심 메서드
    ```
   UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
   ```
    - 사용자의 아이디(보통 이메일)를 받아서 DB에서 해당 사용자를 찾음.
    - 찾으면 UserDetails 객체로 변환해서 반환.
    - 못 찾으면 UsernameNotFoundException 예외 발생.
    - 이 메서드는 "로그인 시 사용자 정보를 어떻게 가져올 것인지?"를 정의하는 역할!

### `SecurityFilterChain` 와 `@PreAuthorize`의 접근 권한에 대한 차이

- 이것들에서 사용자의 접근권한을 확인하고 제한하는 특성을 가지고 있음
- 둘다 사용자의 역할 검증을 하지만, 검증하는 시점이 다르다
- Spring Security 설정(SecurityFilterChain)에서 URL 접근 권한을 설정하면,
    - HTTP 요청이 컨트롤러로 도달하기 전에 차단됨.
- @PreAuthorize("hasRole('ADMIN')")를 컨트롤러에 설정하면,
    - 컨트롤러에 도착한 후에 권한을 체크함.


1. `SecurityFilterChain`에서만 설정한 경우
    - Spring Security가 요청을 필터에서 차단
    - `해당URL`로 오는 요청을 `SecurityFilterChain`에서 ADMIN인지 먼저 확인
    - ADMIN이 아니면 컨트롤러 자체가 실행되지 않음
    - **장점**
        - 성능이 좋음 – 컨트롤러까지 요청이 도달하기 전에 필터에서 차단됨.
        - 보안성이 높음 – 비인가 사용자는 아예 컨트롤러에 접근조차 못함.
2. `@PreAuthorize`에서만 설정한 경우
    - `해당URL`에서 요청이 들어오면 일단 컨트롤러까지 도착함.
    - 컨트롤러 메서드가 실행되기 전에 @PreAuthorize에서 Role을 확인
    - ADMIN이 아니면 403 Forbidden (접근 거부) 발생
    - **장점**
      - 더 정밀한 접근 제어 가능 – 특정 메서드 단위로 다르게 설정할 수 있음.
      - 보안 설정을 컨트롤러에 직접 선언할 수 있어서 코드 가독성이 좋아짐.





# 추가 구현 체크리스트
- 기본적인 사용자 컨트롤
- http 예외 발생시 대처
- sql injection 문제 해결
- jwt 인증(인증, 만료 분리)
- 다중 클라이언트 접속 문제 해결
- aws 배포






