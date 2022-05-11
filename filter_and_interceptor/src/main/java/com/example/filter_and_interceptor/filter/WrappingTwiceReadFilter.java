package com.example.filter_and_interceptor.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class WrappingTwiceReadFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("=== Wrapping TwiceReadFilter ===");
        RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) request);
        log.info("body : {}", requestWrapper.getBody());
        log.info("repeatBody : {}", requestWrapper.getBody());
        chain.doFilter(request, response);
    }
}
