package com.example.reactive_programming;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLOutput;
import java.util.logging.SocketHandler;

@SpringBootApplication
public class ReactiveProgrammingApplication {

    @RestController
    public static class Controller {
        @RequestMapping("/hello")
        public Publisher<String> hello(String name) {
            // publisher 만 만들면, Spring 이 알아서 subscriber 를 호출한다.
            return new Publisher<String>() {
                @Override
                public void subscribe(Subscriber<? super String> sub) {
                    sub.onSubscribe(new Subscription() {
                        @Override
                        public void request(long l) {
                            System.out.println("subscriber : " + sub.getClass());
                            sub.onNext("Hello " + name + ", parameter : " + l + "\n");
                            // sub.onComplete();
                        }
                        @Override
                        public void cancel() {
                        }
                    });
                }
            };
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication.class, args);
    }

}
