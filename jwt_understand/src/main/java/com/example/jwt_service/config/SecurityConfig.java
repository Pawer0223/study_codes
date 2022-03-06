package com.example.jwt_service.config;

import com.example.jwt_service.filter.MyFilter3;
import com.example.jwt_service.jwt.JwtAuthenticationFilter;
import com.example.jwt_service.jwt.JwtAuthorizationFilter;
import com.example.jwt_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class);
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않음.
                .and()
                .addFilter(corsFilter) // 인증이 존재할 때 시큐리티 필터에 등록. 인증이 없다면 @CrossOrigin
                .formLogin().disable() // form 태그로 로그인 안하겠다.
                // but login 요청에 대한 filter 는 적용. authenticationManager 는 WebSecurityConfigurerAdapter 에 존재.
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
                .httpBasic().disable() // httpBasic: authorization 헤더 필드에 id, pw를 담아서 보내는 요청 !
                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**")
                .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRole('ROLE_MANAGER')")
                .anyRequest().permitAll();
    }
}
