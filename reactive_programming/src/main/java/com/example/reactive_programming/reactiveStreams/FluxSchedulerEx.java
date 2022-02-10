package com.example.reactive_programming.reactiveStreams;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.result.method.annotation.PathVariableMapMethodArgumentResolver;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxSchedulerEx {
    public static void main(String[] args) throws InterruptedException {
//        Flux.range(1, 10)
//                .publishOn(Schedulers.newSingle("pub"))
//                .log()
//                .subscribeOn(Schedulers.newSingle("sub"))
//                .subscribe(System.out::println);
//
//        System.out.println("main end");

        log.debug("exit");
        Flux.interval(Duration.ofMillis(200))
                .take(10) // 앞에서 몇개만 받겠다.
                .subscribe(s -> log.debug("onNext: {}", s));

        TimeUnit.SECONDS.sleep(10);


    }
}
