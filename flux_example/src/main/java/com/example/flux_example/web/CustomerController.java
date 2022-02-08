package com.example.flux_example.web;

import com.example.flux_example.domain.Customer;
import com.example.flux_example.domain.CustomerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final Sinks.Many<Customer> sink;

    // A 요청 -> Flux -> Stream
    // B 요청 -> Flux -> Stream
    // 2개의 Stream 을 합쳐서 싱크를 맞춰 줌.
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @GetMapping("/flux")
    public Flux<Integer> flux() {
        return Flux.just(1, 2, 3, 4, 5).delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> fluxstream() {
        return Flux.just(1, 2, 3, 4, 5).delayElements(Duration.ofSeconds(1)).log();
    }

    /**
     * data 가 소진되면 onComplete가 호출된다.
     */
    @GetMapping(value ="/customer", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Customer> findAll() {
        // 1. database 의 data 를 subscribe 한다.
        // 2. 그러면 db 에서는 onSubscribe 를 호출
        // 3. onSubscribe 에서는 request 호출. (unbounded 는 전부 가져오겠다는 뜻.)
        // 4. onNext 를 설정에 맞게 호출.
        // 5. 모든 데이터를 가져오면 onComplete 호출. 이 때, 응답이 된다.

        return customerRepository.findAll().delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping("/customer/{id}")
    public Mono<Customer> findById(@PathVariable Long id) {
        return customerRepository.findById(id).log();
    }

    @GetMapping(value ="/customer/sse")// , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Customer>> findAllSSE() {
        return sink.asFlux().map(c -> ServerSentEvent.builder(c).build()).doOnCancel(() -> {
            sink.asFlux().blockLast(); // 이거 안넣으면 새로고침하면 더이상 요청 못받음.
        });
        // return customerRepository.findAll().delayElements(Duration.ofSeconds(1)).log();
    }

    @PostMapping("/customer")
    public Mono<Customer> save() {
        return customerRepository.save(new Customer("N-GOLO", "Kang Tae")).doOnNext(c -> {
            sink.tryEmitNext(c);
        });
    }
}
