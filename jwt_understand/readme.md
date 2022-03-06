# 사용 기술
- Spring Security
  - 클라이언트 요청에 대한 인증방식을 JWT를 사용하겠다고 설정 해 줌.
    - [SecurityConfig](https://github.com/Pawer0223/study_codes/blob/main/jwt_understand/src/main/java/com/example/jwt_service/config/SecurityConfig.java)
- java-jwt
  - jwt토큰을 생성 및 디코딩을 도와 줌

# Flow
### /login

- 로그인 form을 사용하지 않는다.
- 대신 Security Filter를 재정의 하여 `/login` 요청이 (username, password 를 포함하여) 왔을 때 검증하도록 한다.
  - [JwtAuthenticationFilter]()

### 유저정보 인증 (attemptAuthentication)
- attemptAuthentication 함수에서 loadUserByUsername이 호출된다.
- 직접 DB를 조회하여 요청 사용자 정보를 인증한다.
- 근데 아직 PW 검증이 없다. 이거 확인해서 수정 필수.

### 토큰 발행 (successfulAuthentication)
- 인증이 완료되면, 유저 정보 + SecretKey를 가지고 JWT 토큰을 발행한다.
- 발행된 토큰을 Authorization 필드에 추가하여 응답한다. -> Bearer {token}
  - Bearer는 token을 사용한 인증 방식을 의미 함.
  

### 토큰 발행
권한이 필요한 URL은 Authorization헤더의 Token이 유효한지 확인
- 아직 정리중 ..
