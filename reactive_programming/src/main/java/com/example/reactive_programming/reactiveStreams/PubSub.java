package com.example.reactive_programming.reactiveStreams;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

import static java.util.concurrent.Flow.*;

/**
 *  Publisher.subscribe(Subscriber) <- addObserver
 *
 *  Publisher 가 Subscriber 에게 정보를 줄 때 이 프로토콜을 따라야 함.
 *
 *  프로토콜:
 *      onSubscribe onNext* (onError | onComplete)?
 *
 *  프로토콜 해석:
 *      - onSubscribe 는 반드시 호출해야 한다.(* Subscriber 에게 시그널을 전달하는 역할.)
 *      - onNext 는 무한정 호출 가능.
 *      - onError 나 onComplete 는 둘 중에 하나만 처리 가능.(동시에 불가) 선택 적으로 처리 가능.
 *
 */
public class PubSub {
    // Publisher <- Observable,
    // Subscriber <- Observer

    public static void main(String[] args) {

        Iterable<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);
        ExecutorService es = Executors.newSingleThreadExecutor();

        // 데이터를 주는 쪽.
        Publisher p = new Publisher() {
            @Override
            public void subscribe(Subscriber subscriber) { // 구독자가 나한테 줘. 라고 해당 함수를 호출하게 됨.

                Iterator<Integer> it = itr.iterator();

                subscriber.onSubscribe(new Subscription() {

                    @Override
                    public void request(long n) {
                        es.execute(() -> {
                        try {
                            int i = 0;
                            while (i++ < n) {
                                    if (it.hasNext())
                                        subscriber.onNext(it.next());
                                    else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                }
                               } catch (RuntimeException e) {
                                subscriber.onError(e);
                            }
                        });
                    }
                    @Override
                    public void cancel() {
                    }
                });
            }
        };

        Subscriber<Integer> s = new Subscriber<Integer>() {
            Subscription subscription;
            @Override
            public void onSubscribe(Subscription subscription) { // 필수, subscribe 한 쓰레드 안에서 받도록. 되어 있음..
                System.out.println("onSubscribe");
                this.subscription = subscription;
                this.subscription.request(Long.MAX_VALUE); // 새로운 쓰레드에서 request 날릴 수 없음..
            }

           @Override
            public void onNext(Integer item) { // 옵셔널인데 무한정 가능. <- observer 패턴의 update , 다음 꺼
               System.out.println("onNext " + item);
               // request 의 최소한의 작업이 끝난 후에, 다시 request 할 수 있다는 장점이 있다.
               // 다시 호출하기 전에 현재 상태를 체크해서 조절할 수도 있다.
               this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) { // 어떤 종류의 Exception 이 발생하더라도 onError 를 통해 넘어옴
                System.out.println("onError: " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        p.subscribe(s);
        es.shutdown();


        /**
         *
         * subscriber -> publisher 구독
         *
         * publisher -> subscription 만들어서 -> subscriber.onSubscribe 호출
         *
         * publisher 가 막 던져주는게 아니고,
         *
         * publisher <- subscription -> subscriber 를 중간에 연결해주는 (구독)이라는 정보를 가진 object. 중계 역할
         *
         * subscription 을 통해 요청을 할 수 있다.
         *  - 아니, push 방식인데 왠 요청?
         *  - 얘 의 요청을 backpressure 라고 함.(역압)
         *      - publisher 와 subscriber 사이의 속도차가 발생하는 것을 조절해주는 기능.
         *  - 그래서 request. 를 통해 요청.
         *      - 전부 요청은 Long.MAX_VALUE 를 전달하면 됨.
         *      - return 값이 없음.
         *      - control 하기 위한 역할.
         *
         *
         *
         * 백프레셔
         *  - 나는 데이터를 어떻게 받겠어, 라는 의도를 이야기 하는 것.
         *
         *  - 왜 필요함?
         *      - 어떤 경우 publisher 는 너무 빠름. 데이터를 100만개 보냄.
         *      - 하지만 subscriber 는 하나 처리하는데 1초가 걸림..
         *          - 즉, 속도가 다름.
         *      - 반대의 경우도 존재.
         *
         *   - request 는 onSubscribe 에서 해야 함.
         *
         *
         *  publisher 가 subscriber 한테 쓰레드를 10개만들어서 전송하고 싶어도 스펙상 불가능 하다.
         *  subscriber 는 시퀀셜하게 날라올 것을 기대해서 멀티 쓰레드 문제를 신경쓰지 않음.
         *  하지만 subscriber 의 request 가 동시에 다른 쓰레드에서 처리하는 것은 가능.
         *
         *
        */

    }
}
