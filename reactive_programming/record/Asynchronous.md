# 내용 및 사진 출처
- [토비님 강의 1:24:47 부터 - 비동기 이해하는데 아주 좋네. 기가막힌다](https://www.youtube.com/watch?v=aSTuQiPB4Ns&list=PLOLeoJ50I1kkqC4FuEztT__3xKSfR2fpw&index=4)
- [참조 슬라이드](https://www.slideshare.net/brikis98/the-play-framework-at-linkedin/8-Thread_pool_usageLatencyThread_pool_hell)

# 비동기 처리방식

### 서블릿의 비동기 처리방식

- 서블릿은 기본적으로 Blocking IO (InputStream자체가 Blocking)
- 여러개의 Request를 받게되면, 다른 스레드는 대기했다가 끝나면 처리한다.
 - 스레드가 지연되는 이유 
 - 스레드는 대부분 `[ServletThread1] req -> (logic) -> res(html)` 와 같은 흐름으로 처리 됨.
 - 보통 logic에서 Blocking IO가 발생한다. `[ServletThread1] req -> Blocking IO(DB, API) -> res(html)`
 - 이런 Blocking IO를 별도의 스레드로 처리하게 된다면??
   - 적어도 ServletThread에서 처리되는 나머지 작업은 비동기적으로 처리가가능해진다.
   - [ServletThread1] req -> (WorkThread) -> res(html)
   - 즉, WorkThread를 만들어 지연이 되는 처리를 비동기로 처리함으로써 ServletThread를 쓰레드 풀에 반환하고, 다른 요청을 처리할 수 있다.

> 금방 끝나는 쓰레드에서 오래 걸리는 작업이 처리되고 있을 때, 쓰레드를 하나 더 만들어서 처리해버린다.
> 금방 끝나는 쓰레드를 종료하고 반환할 수 있다는 것이 포인트라고 이해했다.

<img width="799" alt="image" src="https://user-images.githubusercontent.com/26343023/153713394-27191f11-296c-42d8-9374-322b15ec1e55.png">

- client의 요청을 NIO Connector가 받는다.
- NIO Connector가 서블릿 스레드를 계속 만들면서 풀에서 가져온다.
- 이 때 오래걸리는 작업들은 작업쓰레드 풀을 사용해서 실행한다. 그리고 서블릿 쓰레드는 계속 대기하는 것이아니고 바로 리턴한다.
  - 문제는 제대로 된 응답을 못받는다.. Html, Json이든 뭐든.. (일단 리턴!)
  - 응답은, `비동기 서블릿 엔진이 처리`해준다. 작업 쓰레드에서 Return이 되는 시점에 다시 서블릿 쓰레드 풀에서 할당을 한다 !
  - 그리고 아주 빠르게 응답을 처리하는 코드를 실행해서, 여전히 물고 있는 Connection에 응답을 주고 바로 반환 !

> 이러한 메커니즘으로, 적은 쓰레드 풀로 많은 작업을 동시에 처리할 수 있다.
> 무작정 쓰레드가 많다고 좋은것은아니다. 컨텍스트 스위칭이 자주일어나기 때문에. 따라서 지연시키지 않고 적은양의 쓰레드를 사용하는것이 좋다 !
> 작업 쓰레드를 추가함으로써 실현가능.. !

### 이걸 더 응용 한다면??
- 오직 하나의 서블릿 쓰레드만 사용하고, 주요 처리는 모두 Work Thread를 통해 처리한다면, `이 수많은 요청을 처리하는 하나의 서블릿 쓰레드`를 더욱 의미있게 활용할 수 있다.
  - 요청을 빠르게 받고 응답해주고, 뒤에서 일을시키는 역할.

### 전부가 아니다.
- 하지만 단순히 비동기 서블릿을 새롭게 호출한다고 해결할 수 없는 경우가 많다. 
- 서블릿 요청은 바로 사용가능해도 결국 워커 스레드에서 수행되는 I/O와 같은 작업에서 또한 Blocking이 될 수 있다.

### Thread Hell
- 요청이 몰리는 생황이 발생한다.
<img width="526" alt="image" src="https://user-images.githubusercontent.com/26343023/153741159-9dfdb413-8f6b-49f4-9907-0f07ac9e403a.png">

### Thread Hell이 발생하는 이유
<img width="590" alt="image" src="https://user-images.githubusercontent.com/26343023/153742652-7c5e8d1a-5e18-4810-9de8-11804fc84483.png">

- 현대의 서비스 아키텍처 구조는 다른 서버로의 요청이 많이 발생한다. 서비스를 분리하거나, 외부 서비스를 이용하거나. 
  - 이 과정에서 Network I/O가 많이 발생한다. 
- 결국 CPU는 일하지 않고 있는데, 다른 요청이 처리되기를 대기하게된다.
  - 아무리 비동기로 요청을 보내도, 다음 Network 요청을 보내기 위해서는 대기시간이 발생할 수 밖에 없는 아키텍처 구조가 되었다.






# DeferredResult 큐?? (*중요 *핵심)
<img width="695" alt="image" src="https://user-images.githubusercontent.com/26343023/153714784-61c8bbb6-5722-41b1-b610-0bbefb8eebd8.png">

- DeferredResult는 요청에 대한 작업을 수행하지 않고 대기하다가.. 외부에서 발생하는 이벤트에 의해 작업을 수행한 후 결과를 한번에 써준다.
  - 장점?
  - DeferredResult로 어떠한 Object를 keep하고 있다면, 만약 10개의 요청을 각각 DeferredResult에 담아두고있다가 한꺼번에 결과를 출력할 수 있다. 혹은 하나씩도 가능.
    - 요청을 지연해서 출력할 수 있다.
    - 생성 된 DeferredResult Object의 setResult, setException.. 이 호출되기 전까지 응답을 보내지 않고 대기한다.
    - 하지만 서블릿 스레드는 반환한다.
    - 응답을 대기하고 있는 상태에서 클라이언트의 특정 이벤트에 의해, 응답을 보내는 트리거 함수(?)를 호출하는 순간에 응답을 하게된다.
    - 가장 큰 특징은 별도의 WorkThread를 따로만들지 않는다는 것 !
    - Event Driven이나 비동기 처리할때 굉장히 유용.

- 100개의 요청을 1개의 쓰레드로 끝내버렸다.
  - 받고 리턴, 받고 리턴, 받고 리턴 .. 하면서 1개의 쓰레드로 모든 요청을 받았고
  - DeferredResult에서 연결을 유지하고 있기 때문에 별도의 Work Thread를 생성하지도 않고 이벤트가 발생할 때 리턴할 수 있다. (Wow!)

- DeferredResult + Non Blocking I/O로 서버의 자원을 최소화 하면서 많은 양의 작업을 수행할 수 있다..

# Emitter
 - 한번의 요청에, 여러번의 응답을 보낼 수 있도록 할 수 있다.
 - 한번의 요청에 여러번 `emiiter.send`를 보낼 수 있다.
 - SSE표준을 따라 데이터를 streaming방식으로 response한다.
