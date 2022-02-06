package com.example.simple_webflux.filter;

import com.example.simple_webflux.EventNotify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@Slf4j
public class MyFilter2 implements Filter {

    private EventNotify eventNotify;

    @Autowired
    public MyFilter2(EventNotify eventNotify) {
        this.eventNotify = eventNotify;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        log.info("## MyFilter 2 ##");
        eventNotify.add("add data");
    }
}
