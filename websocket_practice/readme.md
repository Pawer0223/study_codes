# WebSocket 직접 사용해보기

- [참조](https://spring.io/guides/gs/messaging-stomp-websocket/)


# STOMP
- STOMP를 사용함으로써 직접 session을 관리하지 않아도되고, pub/sub모델로 통신할 수 있다.

# 어플리케이션의 흐름

- 화면에서 connect를 누르면 server - client가 websocket을 통해 연결.
- 연결이 완료되면, client에서는 특정 URL을 구독한다.
  - 구독한 URL을 모니터링 
하
