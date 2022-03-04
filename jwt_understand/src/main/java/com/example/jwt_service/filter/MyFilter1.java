package com.example.jwt_service.filter;


import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter1 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        System.out.println(this.getClass());
        filterChain.doFilter(request, response);
    }
}
