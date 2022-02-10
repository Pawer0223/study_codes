# 참조
- [토비님 강의](https://www.youtube.com/watch?v=8fenTR3KOJo&list=PLOLeoJ50I1kkqC4FuEztT__3xKSfR2fpw&ab_channel=TobyLee)
- [API Doc](https://projectreactor.io/docs/core/release/api/)

# Iterable
- Java의 for-each는 Collection이 아닌, Iterable 구현체를 대상으로 동작.
- pull 방식, 다음 꺼 줘 !, next()로 가능.

# Observable
- Iterable과 쌍대성(durality) 관계를 가짐.
- push 방식, 이거 가져가라 !, notifyObservers()로 가능.
- Observer(관찰자)에게 새로운 정보가 생기면 넘겨주는 기능을 함.
  - Observable은 여러개의 Observer를 가질 수 있다는 것이 특징.

# Oberver
- 데이터를 받는 객체
  - Observable의 notifyObservers(data)는
  - Observable.addObserver(observer)에 등록 된 Observer의 update에서 받아 처리 가능.

# Observer 패턴의 불편한(?)점
- Complete, 끝(완료)를 구분할 수 있는 방법이 없다.
- Error, 예외 발생 시 회복이 어렵다.

```
Reactive Programming에서 Observer 패턴의2가지 불편한 점을 개선.
```

# Reactive Streams
- [참조1](https://www.reactive-streams.org/)
- [참조2](https://github.com/reactive-streams/reactive-streams-jvm/blob/v1.0.3/README.md#specification)

- 처리 흐름
  - Publisher.subscribe(Subscriber)
  - Publisher.subscribe()함수 내부에서 인자로 받은 Subscriber가 프로토콜에 따라 동작한다.
    - onSubscribe(Subscription)

- Subscriber가 따르는 프로토콜
```
onSubscribe onNext* (onError | onComplete)?
```
  - onSubscribe는 반드시 호출해야 함.(subscriber에 시그널을 전달하는 역할)
  - onNext는 0번이상 무한정 호출 할 수 있다.
  - onError, onComplete는 옵셔널이며, 둘 중 하나만 처리가능하다.(동시에 불가)

# subscription?

- onSubscribe함수의 파라미터로 Subscription의 인스턴스가 전달된다.
- Subscription은 publisher 와 subscriber 사이에 발생할 수 있는 처리속도를 조절해주는 기능을 할 수 있다.
  - 이러한 기능을 하는 것을 backpressure 라고 표현한다.
- Subscription이 Subscriber의 onNext()함수를 호출하게 된다.
  - 몇번 호출할지? 제어할 수 있다.
  - Long.MAX_VALUE는 모든 데이터를 받아오겠다는 의미이다.
- 만약 10건의 데이터 중 2건만 가져오면, 8건은 반복적으로 받아올 수 없다.
  - 다시 요청하기 위해서는 Subscriber의 onNext에서 다시 request를 요청해야 한다.
  - 이렇게 처리하는 이유는 Subscriber의 onNext에서 현재 애플리케이션의 상태를 확인하고, request를 요쳥할수 있다는 장점을 취할 수 있기 때문이다. (* 중요)

# Publisher
- 데이터 스트림을 계속적으로 만들어내는 Provider의 역할

# Subscriber
- Publisher가 보낸것을 받아서 최종적으로 사용

# Operators
- Publisher가 Subscriber에게 데이터를 Push할 때, 중간에서 데이터를 가공하는 기능을 수행할 수 있다.
  - 즉, 하나의 Publisher가 Subscriber에게 데이터를 전송하는 중간에, 데이터를 가공하는 새로운 Publisher를 추가한다.
  - 이 때, 중간에서 데이터 가공의 역할을 Publisher를 Operator라고 이해하자. (일단 강의 기준으로 이해..... 나중에 보완..)
- 인자로 전달받은 Publisher의 subscribe를 새로운 Publisher에서 호출한다.
  - subscribe를 호출하면서 데이터에 함수를 적용한다.



``` java

public static void main(String[] args) {
    Iterable<Integer> iter = Stream.iterate(1, a->a + 1).limit(10).collect(Collectors.toList());
    Publisher<Integer> pub = iterPub(iter);
    Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
    mapPub.subscribe(logSub());
}

// 중계자의 역할을 함.
private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
    return new Publisher<R>() {
        @Override
        public void subscribe(Subscriber<? super R> sub) {
            pub.subscribe(new DelegateSub<T, R>(sub) {
                @Override
                public void onNext(T i) {
                    sub.onNext(f.apply(i));
                }
            });
        }
    };
}
```

- DelegateSub 클래스는 전달 받은 Subscriber의 함수를 다시 호출하는 것이 전부이다.
```java
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DelegateSub<T, R> implements Subscriber<T> {

    Subscriber sub;

    public DelegateSub(Subscriber<? super R> sub) {
        this.sub = sub;
    }
    @Override
    public void onSubscribe(Subscription s) {
        sub.onSubscribe(s);
    }
    @Override
    public void onNext(T i) {
        sub.onNext(i);
    }
    @Override
    public void onError(Throwable throwable) {
        sub.onError(throwable);
    }
    @Override
    public void onComplete() {
        sub.onComplete();
    }
}

```

# Schedulers

- publisher의 동작을 별도의 쓰레드에서 처리하고 싶다.
  - request에서 -> onNext()의 호출.
- Scheduler를 통해 해결할 수 있다. reactive-programming에서 scheduler는 아래 2가지 함수 호출을 통해 지정할 수 있다.

### 1. publishOn
- subscriber가 느린 경우에, 데이터를 받는쪽에서 별도의 쓰레드를 생성해서 처리할 수 있다.
<img width="426" alt="image" src="https://user-images.githubusercontent.com/26343023/153431663-eb76b643-cea0-4c06-9d72-1876f8f57aa5.png">

### 2. subscribeOn
- publisher가 느린경우 사용하면 좋다.(blocking I.O ...)
- 동그라미 하나당 onNext를 통해 data를 보내는 동작.
- subScribeOn에 전달되는 스케줄러를 통해 그림 상단의 요청들을 처리하게 됨.
<img width="401" alt="image" src="https://user-images.githubusercontent.com/26343023/153426486-7c717733-24d2-437c-a890-e4df2e5d989b.png">

# User Thread 와 Demon Thread
- User Thread는 하나라도 살아있다면 종료하지 않고 계속 진행.
- Demon Thread는 User Thread가 하나도 없다면 종료.
  - Flux.interval은 데몬 쓰레드로 동작 함 !

