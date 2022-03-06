package com.example.jwt_service.filter;


import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class MyFilter3 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        if (req.getMethod().equals("POST")) {
            String headerAuth = req.getHeader("Authorization");
            if (headerAuth.equals("test")) {
                log.info("Authorization Header --> {}", headerAuth);
                // filterChain.doFilter(req, res);
            } else {
                PrintWriter writer = res.getWriter();
                writer.println("token must me test");
                return ;
            }
        }
        filterChain.doFilter(req, res);
    }
}
