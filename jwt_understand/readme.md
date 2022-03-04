# 사용 기술
- Spring Security
  - 클라이언트 요청에 대한 인증방식을 JWT를 사용하겠다고 설정 해 줌.
    - [SecurityConfig](https://github.com/Pawer0223/study_codes/blob/main/jwt_understand/src/main/java/com/example/jwt_service/config/SecurityConfig.java)
- java-jwt
  - jwt토큰을 생성 및 디코딩을 도와 줌

# Flow
- 로그인 -> 토큰 발행
- 권한이 필요한 URL은 Authorization헤더의 Token이 유효한지 확인
- 아직 정리중 ..
