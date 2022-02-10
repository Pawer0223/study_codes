package com.example.reactive_programming.reactiveStreams;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Operators
 *  : Publisher --- (Data) --> Operator(Data 가공) ---> Subscriber(가공 된 데이터)
 *
 *
 *  pub -> data1 -> mapPub -> data2 -> logSub
 *                  <-  subscribe(logSub)
 *                  ->  onSubscribe(s)
 *                  ->  onNExt
 *                  ->  onNExt
 *                  ->  onComplete
 *
 *  Down Stream: 좌 -> 우,
 *  Up Stream: 우 -> 좌
 */

@Slf4j
public class PubSub2 {

    private static void callMapPub() {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a->a + 1).limit(10).collect(Collectors.toList()));

        // mapPub 에서 pub 의 subscribe 를 호출.
        Publisher<Integer> mapPub =  mapPub(pub, (Function<Integer, Integer>) s -> s * 10);
        // 한번 더 가공
        Publisher<Integer> map2Pub =  mapPub(mapPub, (Function<Integer, Integer>) s -> -s);
        Subscriber<Integer> sub = logSub();
        map2Pub.subscribe(sub);
    }

    public static void main(String[] args) {
        Iterable<Integer> iter = Stream.iterate(1, a->a + 1).limit(10).collect(Collectors.toList());
        Publisher<Integer> pub = iterPub(iter);
        // Publisher<Integer> sumPub = sumPub(pub);
        // Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
        // Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");
        // Publisher<String> reducePub = reducePub(pub, "", (BiFunction<String, Integer, String>)(a, b) -> a + " - " + b);
        Publisher<StringBuilder> reducePub = reducePub(pub, new StringBuilder(),
                (a, b) -> a.append(b + ","));
        reducePub.subscribe(logSub());
    }

    private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSub<T, R>(sub) {
                    R result = init;
                    @Override
                    public void onNext(T i) {
                        result = bf.apply(result, i);
                    }
                    @Override
                    public void onComplete() {
                        sub.onNext(result);
                        sub.onComplete();
                    }
                });
            }
        };
    }

    // reduce 흐름
    // 1, 2, 3, 4, 5
    // 0 (초기 값) -> (0, 1) -> 0 + 1 = 1
    // 1         -> (1, 2) -> 1 + 2 = 3
    // 3         -> (3, 3) -> 3 + 3 = 6
    // 6         -> (6, 4) -> 6 + 4 = 10
    // 10        -> (10, 5) -> 10 + 5 = 15

//    private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> sub) {
//                pub.subscribe(new DelegateSub(sub) {
//                    int sum = 0;
//                    @Override
//                    public void onNext(Integer i) {
//                        sum += i;
//                    }
//                    @Override
//                    public void onComplete() {
//                        sub.onNext(sum);
//                        sub.onComplete();
//                    }
//                });
//            }
//        };
//    }

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

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T i) {
                log.debug("onNext: {}", i);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError: {}", t);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        };
    }

    private static Publisher<Integer> iterPub(Iterable<Integer> iter) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
                        try {
                            iter.forEach(s -> subscriber.onNext(s));
                            subscriber.onComplete();
                        } catch (Throwable t) {
                            subscriber.onError(t);
                        }
                    }
                    @Override
                    public void cancel() {
                    }
                });
            }
        };
    }

}
