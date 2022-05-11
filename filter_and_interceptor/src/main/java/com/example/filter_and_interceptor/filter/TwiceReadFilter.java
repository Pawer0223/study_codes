package com.example.filter_and_interceptor.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class TwiceReadFilter implements Filter {

    private String readBody(HttpServletRequest request) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String buffer;
        while ((buffer = input.readLine()) != null) {
            builder.append(buffer.trim());
        }
        return builder.toString();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("=== TwiceReadFilter ===");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String body = readBody(httpRequest);
        String repeatBody = readBody(httpRequest);
        log.info("body : {}", body);
        log.info("repeatBody : {}", repeatBody);
        chain.doFilter(request, response);
    }
}
