# Spring Reactive Web
- Netty서버로 단일스레드, 비동기로 동작한다.
- Mono, Flux 데이터 타입 사용

# R2DBC
- RDBMS 비동기 처리를 도와주는 라이브러리
- 요런식으로 쓰네?

```java
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    @Query("SELECT * FROM customer WHERE last_name = :lastname")
    Flux<Customer> findByLastName(String lastName);
}
```

# 처리 과정
``` java

   /**
     * data 가 소진되면 onComplete가 호출된다.
     */
    @GetMapping(value ="/customer", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Customer> findAll() {
        return customerRepository.findAll().delayElements(Duration.ofSeconds(1)).log();
    }

```

<img width="487" alt="image" src="https://user-images.githubusercontent.com/26343023/152750389-fc7c2010-e4e1-4e4c-a9d2-0fc2c1d38416.png">

1. database 의 data 를 subscribe 한다.
2. 그러면 db 에서는 onSubscribe 를 호출
3. onSubscribe 에서는 request 호출. (unbounded 는 전부 가져오겠다는 뜻.)
4. onNext 를 설정에 맞게 호출.
5. 모든 데이터를 가져오면 onComplete 호출. 이 때, 응답이 된다.
  - 즉, 모든 데이터를 소진하면 통신이 종료.

# 이제 좀 이어지네
- 앞에서 요청을 대기 하면서, 실시간으로 갱신하는 과정이 결국 WebFlux에서 요청을 처리할 때 처리되는 메커니즘이다.

# Sink
- 이거는 각 요청의 Stream을 한개로 합쳐서 싱크를 맞추는 것.
- 일단 알아만 두자. Sink로 sse프로토콜 통신을 구현했다.
  - 즉, 모든 데이터가 소진하더라도 통신이 종료되지 않는다.
  - sse를 호출하고, post요청으로 데이터를 추가하면, 실시간으로 추가되는것을 볼 수 있다.
- 이거는 몽고DB의 @Tailable쓰는 것과 유사한 기능을 한다.


