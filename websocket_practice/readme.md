# WebSocket 직접 사용해보기

- [참조](https://spring.io/guides/gs/messaging-stomp-websocket/)


# STOMP
- STOMP를 사용함으로써 직접 session을 관리하지 않아도되고, pub/sub모델로 통신할 수 있다.

# 정리

### application 흐름이해

- 화면에서 connect를 누르면 server - client의 websocket이 생성된다.(연결 됨.)

- 연결이 완료되면 client는 데이터 스트림을 통한 통신을 하기 위해 특정 URL을 구독한다.
  - stompClient.subscribe('/subscribe/greetings', (...) {...};);`

- 이름을 입력하고 send를 누르는 행위는 연결 된 데이터 스트림에 데이터를 적재하는 과정이다.
  - `stompClient.send("/publish/hello", ... );`

- 서버에서는 연결 된 client의 요청을 설정된 prefix를 통해 인지할 수 있다.

- 서버는 요청을 받고, 새로운 데이터를 만들어서 데이터 스트림에 저장한다. (현재 application에서 그렇게 진행하고 있음.)
  - SendTo("/subscribe/greetings")

- 데이터 스트림에 데이터가 전달되면, 구독하고 있는 client에서는 스트림의 데이터를 가져쓸수 있다.


``` java
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/publish");
        config.setApplicationDestinationPrefixes("/subscribe");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket-end-point").withSockJS();
    }

}
```

- connect버튼 클릭시 핸드쉐이킹 과정이 수행되며 server <-> client간 연결을 오픈한다.
  - connect를 위해 요청하는 URL은 server에서 설정해 놓은 URL이다.
  - `registry.addEndpoint("/gs-guide-websocket").withSockJS();`

- client -> server로 데이터를 전송하는 행위
  - publish라고 표현하겠다.
  - publish요청은 서버의 enableSimpleBroker에 설정한 url을 prefix로 식별할 수 있다.
  - ex) /publish/hello 요청은, @MessageMapping("/hello")와 매핑된다.

- server -> client로 데이터를 전송하는 행위
  - subscribe로 표현하겠다.
  - client입장에서는 server에서 데이터를 받기위한 설정이 필요하다. server에서 /publish로 요청을 받은것처럼.
  - `stompClient.subscribe(...)`
  - ex) @SendTo("/subscribe/greetings")의 서블릿이 호출되면, 그 결과를 client에서 실시간으로 받고, callback function을 처리하게 된다.

``` javascript
function connect() {
    const socket = new SockJS('/socket-end-point');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        stompClient.subscribe('/subscribe/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}
```
