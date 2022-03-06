package com.example.jwt_service.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

        // 1. username, password 받아서

        // 2. 계정체크, authenticationManager 에서 로그인 시도를 하게되면, PrincipalDetailsService 가 호출 됨.(loadUserByUsername)

        // 3. principalDetails 세션에 담음. -> 권환관리를 위해

        // 4. JWT 토큰 만들고 응답.

        return super.attemptAuthentication(request, response);
    }
}
