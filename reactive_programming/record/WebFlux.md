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


