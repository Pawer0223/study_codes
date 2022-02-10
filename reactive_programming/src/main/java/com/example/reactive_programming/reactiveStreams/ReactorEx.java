package com.example.reactive_programming.reactiveStreams;

import reactor.core.publisher.Flux;

public class ReactorEx {
    public static void main(String[] args) {
        // 일종의 publisher
        Flux.<Integer>create(e -> {
            e.next(1);
            e.next(2);
            e.next(3);
            e.complete();
        })
        .log()
        .map(s->s * 10)
        .reduce(0, (a,b)->a+b)
        .log()
        .subscribe(System.out::println);
    }
}
