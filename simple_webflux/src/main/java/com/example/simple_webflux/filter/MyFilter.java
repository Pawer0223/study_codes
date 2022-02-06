package com.example.simple_webflux.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Formatter;

@Slf4j
public class MyFilter implements Filter {


    /**
     *  contentType: text/event-stream
     *  화면에 데이터를 1초에 하나씩 출력한다.
     *
     *  text/plain의 경우, 5초가 지난 후 한번에 출력하고 있다.
     */
    private void basicWebFluxStructre(ServletResponse response) throws Exception {
        HttpServletResponse servletResponse = (HttpServletResponse)response;

        // servletResponse.setContentType("text/plain; charset=UTF-8");
        servletResponse.setContentType("text/event-stream; charset=UTF-8");
        PrintWriter out = servletResponse.getWriter();

        for (int i = 0; i < 5; i++) {
            out.printf("[%d] 번째 응답 : ", i);
            out.println(LocalDateTime.now());
            out.flush(); // buffer clear
            Thread.sleep(1000);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
      log.info("## MyFilter.doFilter ##");

        HttpServletResponse servletResponse = (HttpServletResponse)response;

        try {
            basicWebFluxStructre(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
