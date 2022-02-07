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
public class MyFilter implements Filter {

    private EventNotify eventNotify;

    @Autowired
    public MyFilter(EventNotify eventNotify) {
        this.eventNotify = eventNotify;
    }

    /**
     * 무한 루프안에서 eventNotify의 상태를 체크한다.
     * eventNotify.change = true일 때, 화면에 출력한다.
     * eventNotify.change는 add할 때 true로 변경된다.
     *
     * sse emitter와 같은 라이브러리로 편리하게 구현할 수 있음.
     */
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

    /**
     *  contentType: text/event-stream => 화면에 데이터를 1초에 하나씩 출력.
     *  text/plain => 5초 후 한번에 출력
     *
     *  reactive streams 라이브러리를 통해 표준을 지키며 응답할 수 있도록 가능하다.
     */
    private void basicWebFluxStructure(ServletResponse response) throws Exception {
        HttpServletResponse servletResponse = (HttpServletResponse)response;

        servletResponse.setContentType("text/plain; charset=UTF-8");
       //  servletResponse.setContentType("text/event-stream; charset=UTF-8");
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
            // loop(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // WebFlux -> Reactive Streams 가 적용된 stream. (단일 스레드, 비동기 처리) -> 단일 스레드가 효과적.
    // Servlet MVC -> Reactive Streams 가 적용된 stream. (멀티 스레드)

}
