package com.example.webflux_understand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
@Slf4j
public class WebfluxUnderstandApplication {

    @GetMapping("/")
    Mono<String> hello() {
        log.info("pos1");
        // Mono m = Mono.just(generateHello()).doOnNext(c->log.info(c)).log();
        Mono m = Mono.fromSupplier(() -> generateHello()).doOnNext(c->log.info(c)).log();
        /*
        Mono origin = Mono.fromSupplier(
                new Supplier<String>() {
                    @Override
                    public String get() {
                        return generateHello();
                    }
                }
        );
        */

        // 아래 코드는 cold 로 동작. 즉 체인 걸어논게 똑같이 동작
        // m.subscribe();
        log.info("pos2");
        return m;
    }

    @GetMapping("event/{id}")
    Mono<List<Event>> event(@PathVariable long id) {
        List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2"));
        return Mono.just(list);

    }

    @GetMapping(value="events", produces = MediaType.TEXT_EVENT_STREAM_VALUE) // stream 방식으로 리턴
    Flux<Event> events() {
        Stream<Event> stream = Stream.generate(() -> new Event(System.currentTimeMillis(), "value"));
        return Flux
                //.fromStream(stream)
                //.<Event>generate(sink->sink.next(new Event(System.currentTimeMillis(), "value"))) // 싱크대, 하수를 흘려보냄
                .<Event, Long>generate(() -> 1L, (id, sink) -> {
                    sink.next(new Event(id, "value :" + id));
                    return id + 1;
                })
                .delayElements(Duration.ofSeconds(1))
                .take(10);
    }

    private String generateHello() {
        log.info("method generateHello()");
        return "Hello Mono";
    }

    @Data
    @AllArgsConstructor
    public static class Event {
        long id;
        String value;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebfluxUnderstandApplication.class, args);
    }
}
