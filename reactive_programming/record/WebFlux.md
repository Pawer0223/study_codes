# WebClient
* AsyncRestTemplate 과 유사한 것
* Builder Style
* Mono 를 반환.
	* ResponseEntity와 유사.
* Mono는, Publisher이다. (Publisher Interface를 구현)
	* `즉, Subscriber를 구독하지 않으면 시작하지 않음 !`
		* Publisher는 Subscriber가 subscribe할 때 처리가 시작됨.
		* Publisher.subscribe(Subcriber) 하면
		* Subscriber.onSubscribe(..) 부터 시작 됨.
* WebFlux에서 subscribe함수의 호출은 Spring이 대신해 준다.
	* 언제?
	* Mono 또는 Flux객체를 반환할 때
	* rest()메서드 에서는 2개의 외부 API와 1개의 내부 API를 호출한다.
		* 하지만 모두 비동기를 처리할 수 있는 객체로 반환하기 때문에(Mono, CompletableFuture) 어디서도 Blocking되지 않는다.
		* 결과적으로 Non-Blocking 코드를 깔끔하게 작성할 수 있다.
``` java

	...

static final String URL1 = "http://localhost:8081/service?req={req}"; // 외부 서비스 1

static final String URL2 = "http://localhost:8081/service2?req={req}"; // 외부 서비스 2

@Autowired
MyService myService; // 내부 서비스

WebClient client = WebClient.create();

@GetMapping("/rest")
public Mono<String> rest(int idx) {
	return client.get().uri(URL1, idx).exchange() // Mono<ClientResponse>
			.flatMap(c -> c.bodyToMono(String.class)) // Mono<String>
			.flatMap((String res1) -> client.get().uri(URL2, res1).exchange()) // Mono<ClientResponse>
			.flatMap(c -> c.bodyToMono(String.class)); // Mono<String>
			.flatMap(res2 -> Mono.fromCompletionStage(myService.work(res2));
// CompletableFuture<String> -> Mono<String>
	...
}

@Service
public static class MyService {
	@Async
	public CompletableFuture<String> work(String req) {
		return CompletableFuture.completeFuture(req + "/asyncwork");
	}

}
```


# Reactive
* Flux라는 것은 Stream인데, Stream은 데이터가 계~속 흘러오도록 되어있는것. 시냇물 처럼
* Reactive의 출발은 Event Driven 스타일의 프로그래밍을 위해 시작 !

# 이해하기

- 출력순서 ?
``` java

    @GetMapping("/")
    Mono<String> hello() {
        log.info("pos1");
        Mono m = Mono.just("Hello WebFlux").log();
        log.info("pos2");

        return m;
    }

```


<details>
  <summary>Answer</summary>
	<h3>Mono객체를 Spring이 return 받으면 subscribe함수를 호출한다.</h3>
<li>2022-02-14 16:41:03.130  INFO 44037 --- [ctor-http-nio-2] c.e.w.WebfluxUnderstandApplication       : pos1</li>
<li>2022-02-14 16:41:03.132  INFO 44037 --- [ctor-http-nio-2] c.e.w.WebfluxUnderstandApplication       : pos2</li>
<li>2022-02-14 16:41:03.147  INFO 44037 --- [ctor-http-nio-2] reactor.Mono.Just.1                      : | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)</li>
<li>2022-02-14 16:41:03.149  INFO 44037 --- [ctor-http-nio-2] reactor.Mono.Just.1                      : | request(unbounded)</li>
<li>2022-02-14 16:41:03.149  INFO 44037 --- [ctor-http-nio-2] reactor.Mono.Just.1                      : | onNext(Hello WebFlux)</li>
<li>2022-02-14 16:41:03.153  INFO 44037 --- [ctor-http-nio-2] reactor.Mono.Just.1                      : | onComplete()</li>
</details>

- 그럼 아래의 경우, Service는 Return 되고 실행?
``` java

    @GetMapping("/")
    Mono<String> hello() {
        log.info("pos1");
        Mono m = Mono.just(myService.findById(1L)).log();
        log.info("pos2");
        return m;
    }

```
<details>
  <summary>Answer</summary>
	실행은 먼저되고, subscribe시점에 가져다 쓰게 됨. But Callback스타일로 나중에 실행되게도 처리할 수 있다.(Mono.fromSupplier( ... 함수 전달 ... ))
</details>

- 이 경우는 어케될까?

``` java
    @GetMapping("/")
    Mono<String> hello() {
        log.info("pos1");
        Mono m = Mono.fromSupplier(() -> generateHello()).doOnNext(c->log.info(c)).log();
        m.subscribe();
        log.info("pos2");
        return m;
    }
```

<details>
  <summary>Answer</summary>
	<h3>hot source 방식으로 호출 됨.</h3>
	<li>pos1</li>
	<li>Mono객체 m에 걸린 체인이 그대로 m.subscribe()에서 실행</li>
	<li>pos2</li>
	<li>spring에 의해 subscribe한번 더 호출</li>
</details>

# Publihser 의 hot, cold 소스
- hot: 실시간으로 일어나는 데이터들, 구독하는 시점부터 실시간으로 발생하는 데이터만 가지고 옴
- cold: 데이터가 고정, 어느 subscriber든지 동일한 결과가 셋팅 됨.
