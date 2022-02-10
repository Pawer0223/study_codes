package com.example.reactive_programming.reactiveStreams;

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
