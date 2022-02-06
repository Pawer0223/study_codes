package com.example.simple_webflux.filter;

import com.example.simple_webflux.EventNotify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Formatter;

@Slf4j
public class MyFilter implements Filter {

    private EventNotify eventNotify;

    @Autowired
    public MyFilter(EventNotify eventNotify) {
        this.eventNotify = eventNotify;
    }

    /**
     *  contentType: text/event-stream
     *  화면에 데이터를 1초에 하나씩 출력한다.
     *
     *  text/plain의 경우, 5초가 지난 후 한번에 출력하고 있다.
     */
    private void basicWebFluxStructure(ServletResponse response) throws Exception {
        HttpServletResponse servletResponse = (HttpServletResponse)response;

        // servletResponse.setContentType("text/plain; charset=UTF-8");
        servletResponse.setContentType("text/event-stream; charset=UTF-8");
        PrintWriter out = servletResponse.getWriter();

        for (int i = 0; i < 5; i++) {
            eventNotify.add("add data");
            out.printf("[%d] 번째 응답 : ", i);
            out.println(LocalDateTime.now());
            out.flush(); // buffer clear
            Thread.sleep(1000);
        }
        eventNotify.changeChange();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
      log.info("## MyFilter 1 ##");

        HttpServletResponse servletResponse = (HttpServletResponse)response;

        try {
            basicWebFluxStructure(response);
            loop(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loop(ServletResponse response) throws Exception {

        HttpServletResponse servletResponse = (HttpServletResponse)response;
        servletResponse.setContentType("text/event-stream; charset=UTF-8");
        PrintWriter out = servletResponse.getWriter();

        while (true) {
            try {
                if (eventNotify.isChange()) {
                    out.printf("[%d] 번째 응답 : ", eventNotify.getEvents().size() - 1);
                    out.println(LocalDateTime.now());
                    out.flush(); // buffer clear
                    eventNotify.changeChange();
                }
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
