package com.example.reactive_streams;


import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.Arrays;

/**
 *  2. 유튜버는 사용자가 구독을 눌렀을 때, 시청하지 않은 영상정보를 기록한다.
 *  3. 구독자는 추천영상을 받아볼 수 있다. 몇개씩 받아볼지는 스스로 정한다.
 *
 */
public class MyPub implements Publisher<Integer> {

    Iterable<Integer> myVideos = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        System.out.println("[구독자]: 당신의 채널을 구독하겠습니다.");
        MySubscription subscription = new MySubscription(subscriber, myVideos);
        System.out.println("[유튜버]: 구독해주셔서 감사합니다. 제가 업로드한 영상 리스트를 확인하실 수 있습니다.");
        subscriber.onSubscribe(subscription);
    }
}
