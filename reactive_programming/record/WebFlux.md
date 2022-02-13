# WebClient
- AsyncRestTemplate 과 유사한 것
- Builder Style
- Mono<ClientResponse> 를 반환.
    - ResponseEntity와 유사.
- Mono는, Publisher이다. (Publisher Interface를 구현)
    - `즉, Subscriber를 구독하지 않으면 시작하지 않음 !`
        - Publisher는 Subscriber가 subscribe할 때 처리가 시작됨.
        - Publisher.subscribe(Subcriber) 하면
        - Subscriber.onSubscribe(..) 부터 시작 됨.
- WebFlux에서 subscribe함수의 호출은 Spring이 대신해 준다.
    - 언제?
    - Mono 또는 Flux객체를 반환할 때
   
    
