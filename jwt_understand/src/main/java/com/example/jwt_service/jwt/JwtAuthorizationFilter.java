package com.example.jwt_service.jwt;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt_service.auth.PrincipalDetails;
import com.example.jwt_service.model.User;
import com.example.jwt_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// BasicAuthenticationFilter 는 Security 에서 제공하는Filter,
// 권한 및 인증이 필요한 URL 요청이 오면 항상 타도록 함. (권한 및 인증이 필요없다면 타지않음.)
    // SecurityConfig 에서 걸러지는 URL 들
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("권한이나 인증이 필요한 필터");
        String authHeader = request.getHeader("Authorization");
        log.info("authHeader: " + authHeader);
        // header check
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return ;
        }
        // token validate
        String jwt = authHeader.replace("Bearer ", "");
        String name =
                JWT.require(Algorithm.HMAC512("N-GO-LO")).build().verify(jwt).getClaim("name").asString();
        // success
        if (name != null) {
            User user = userRepository.findByName(name);
            PrincipalDetails principalDetails = new PrincipalDetails(user);

            log.info("authorities : " + principalDetails.getAuthorities());

            // authentication 객체 강제로 만들기. 권한정보를 세션에 넣어준다. 따라서 pw 는 null 이어도 현재 로직상은 문제없음.
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails,null, principalDetails.getAuthorities()
            );

            // 강제로 security session 에 접근해서 Authentication 객체 저장.
            // why ?
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        }
    }
}
