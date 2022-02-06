package com.example.reactive_streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;

/**
 *  구독자가, 받아볼 수 있는 데이터를 보유하고 있다가 정해진 갯수만큼 동영상을 추천해준다. (onNext)
 *  더 이상 추천할 동영상이 없다면, 다 봤다고 알려주고 더이상 추천을 하지 않는다.
 */
public class MySubscription implements Subscription {

    private Subscriber s;
    private Iterator<Integer> data;

    public MySubscription(Subscriber s, Iterable<Integer> data) {
        this.s = s;
        this.data = data.iterator();
    }

    @Override
    public void request(long l) {
        while (l > 0) {
            if (data.hasNext()) {
                s.onNext(data.next());
            } else {
                s.onComplete();
                break ;
            }
            l--;
        }
    }

    @Override
    public void cancel() {

    }
}
