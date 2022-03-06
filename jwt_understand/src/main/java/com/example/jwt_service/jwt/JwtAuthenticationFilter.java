package com.example.jwt_service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt_service.auth.PrincipalDetails;
import com.example.jwt_service.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

// security 에서 제공하는 filter
// login 요청 (with username, password) 시 해당 filter 가 동작함.
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // login 요청 시, 로그인을 시도하는 함수.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.info("attemptAuthentication");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(request.getInputStream(), User.class);
            log.info("User : {}", user);
            // token 생성, username, pw를 통해 토큰을 생성.
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword());
            // PrincipalDetailsService.loadUserByUsername(String username) 실행 -> 로그인 정보 반환
            // DB를 조회하여, 인증작업 수행.
            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            log.info(principalDetails.getUser().getName());
            // 반환한 Authentication 객체를 세션에 저장하게 됨. (Session)
            // 굳이 JWT 토큰 사용시에는 세션을 만들 이유가 없다. but 권한관리를 위해 저장.
            return authentication;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // attemptAuthentication 이 후, 정상 수행 시 호출되는 함수, JWT 토큰을 만들어 응답한다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {

        log.info("successfulAuthentication");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        User user = principalDetails.getUser();
        String secretKey = "N-GO-LO";

        // Hash 암호화 방식
        String jwtToken = JWT.create()
                .withSubject("TaeSanToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 10)))
                .withClaim("id", user.getId()) // 사용자 정의 claim
                .withClaim("name", user.getName())
                .sign(Algorithm.HMAC512(secretKey));

        // 응답헤더에 token 추가
        response.addHeader("Authorization", "Bearer " + jwtToken);
    }
}
