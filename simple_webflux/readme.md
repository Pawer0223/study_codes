# 결과

- 1초에 한번씩 화면에 문자열을 출력할 수 있다.
- 데이터가 추가되면 바로바로 화면에서 갱신된다.(새로고침 없이)

# 검증 과정

```
(run) -> SimpleWebfluxApplication
(Web Browser) -> localhost:8080/sse

  [0] 번째 응답 : 2022-02-07T14:15:05.844621

  ...

  [4] 번째 응답까지 1초에 한번씩 출력된다.

(새로운 Web Browser) -> localhost:8080/add
  
  localhost:8080/add 호출 시 실시간으로 데이터가 추가되는지 확인한다. (/sse 에서 확인)

```

# 핵심 로직

``` java
servletResponse.setContentType("text/event-stream; charset=UTF-8");
```
- contentType을 text/event-stream으로 지정한다.
- 단순 text/plain으로 지정하면 5초 후에 한번에 출력된다. (심지어 loop가 돌아갈 땐 요청이 끊나지 않기 때문에, 화면에 아무것도 출력이 되지 않음.)

``` java

private void loop(ServletResponse response) throws Exception {

    HttpServletResponse servletResponse = (HttpServletResponse)response;
    servletResponse.setContentType("text/event-stream; charset=UTF-8");
    PrintWriter out = servletResponse.getWriter();

    while (true) {
        try {
            if (eventNotify.isChange()) {
                out.printf("[%d] 번째 응답 : ", eventNotify.getEvents().size() - 1);
                out.println(LocalDateTime.now());
                out.flush(); // buffer clear
                eventNotify.changeChange();
            }
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```
- 무한 루프안에서 eventNotify.isChange()일 때, 화면에 출력한다.
- isChange가 true가 되는것은 /add를 호출할 때이다.
- 출력 후, 다시 false로 변경한다.

# 개선 가능한점
- 실시간 통신을 위한 응답을 표준화하여 보낼 수 있다. -> Reactive Streams 사용
- 요청을 대기하고 있는 loop함수의 역할 -> SSE Emitter를 통해 개선 가능.

# Next

- Reactive Streams 사용해보자.
