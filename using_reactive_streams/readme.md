# 처리과정

- publisher.subscribe(subscriber)하면, -> 구독자가 출판사를 구독하면
- Subscription 구독 정보를 만든다, -> A구독자가 [1, 2, 3 .. 10] 의 신문을 읽을 수 있다.
  - 구독정보에는 데이터와, 구독자 정보, 구독자의 요청을 처리할 행위(request)가 있다.
- Subscriber.onSubscribe(subscription), 구독자는 구독정보를 가지고 request 한다.
  - request에 하루에 받아볼 신문의 양을 정의한다. 이 갯수만큼 신문정보가 제공된다.
- 정해진 갯수만큼 데이터를 제공(request)하게 된다. 데이터는 구독자에게 onNext(data)로 전달한다.
- onNext에서는 데이터를 받아서 처리한다.
- 예제에서는 onNext에서 정해진 갯수만큼 data를 전달 받은 경우 다시 request를 호출해서 재귀한다.
  - 즉, 모든 데이터를 다 소진할때까지 돌게된다.


# 핵심
<img width="354" alt="image" src="https://user-images.githubusercontent.com/26343023/152739274-b8623a5e-8f02-4348-b96e-009533cb3eef.png">

- 실제 reactive_streams에서 제공되는 interface는 4개가 전부다.
  - processor는 Publisher와 Subscriber를 하나의 클래스에 모아둔 클래스. 

- reactive_streams에서 제공하는 interface를 재정의해서 사용했다.
  - MyPub, MySub MySubscription을 만들었다.
  - pub.subscribe(subscriber)만 호출하면 결과에 기재 된 작업이 모두 순차적으로 처리되고 있다.


# 결과
<img width="623" alt="image" src="https://user-images.githubusercontent.com/26343023/152739890-630421ae-645d-40e1-82d8-7f2e830a5cee.png">


# ?

- simple_webflux 구현했을 때보다 나은점이 느껴지지 않네? 공통점이 없어 뷔는데
  - 이 예제는 WebFlux에서 내부적으로 처리되는 reactive_streams의 동작방식을 이해하기 위함.
  - simple_webflux와 비교는 아직은 무의미.

- 그니깐 reactive_streams가 어떻게 처리되는지에 집중하자.

# Next

- WebFlux 사용해보기
