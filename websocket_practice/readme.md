# WebSocket 직접 사용해보기

- [참조](https://spring.io/guides/gs/messaging-stomp-websocket/)

# 결과
![websocket_practice](https://user-images.githubusercontent.com/26343023/153161739-dfd3f41d-9e24-4214-8fd8-c17cb0647bff.gif)


# STOMP
- STOMP를 사용함으로써 직접 session을 관리하지 않아도되고, pub/sub모델로 통신할 수 있다.

# 어플리케이션의 흐름

- 화면의 [connect] 클릭
  - 웹 소켓을 만들고, 서버 <-> 클라이언트를 연결한다.

- connect가 완료되면 client는 데이터를 퍼오기 위해 URL로 subscribe를 수행한다.
  - client는 subscribe한 URL에 매핑되는 서블릿의 실행결과를 실시간으로 전달받을 수 있다.

- 사용자 이름을 입력하고 [send] 클릭
  - 연결 된 서버로 요청을 보낸다.
  - 서버에서는 URL의 prefix 설정을 통해, 메시지 브로커의 데이터를 전달 받을 수 있게된다.
  - prefix를 제외한 요청은 @MessageMapping과 매핑된다.
  - 아래와 같이 코드가 작성되어 있을 때 `/publish/hello` 요청이 들어오면
    - MessageBroker를 통해 데이터를 받는다.
    - @MessageMapping("/hello")로 매핑되어 greeting함수가 호출된다.

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/publish");
        config.enableSimpleBroker("/subscribe");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // registry.addEndpoint("/gs-guide-websocket").withSockJS();
        registry.addEndpoint("/socket-end-point").withSockJS();
    }

}

```

```java
@Controller
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/subscribe/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
}
```

- [send]요청으로 /publish/hello 호출되어 greeting함수가 호출되었다.
  - greeting함수의 @SendTo("/subscribe/greetings") 어노테이션에 의해 해당 Destination을 구독하는 Client에게 메시지가 뿌려진다.

