package com.example.reactive_streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * 4. 구독자는 채널을 구독하면, 자신이 초깃값으로 정해놓은 갯수의 동영상을 매일 추천 받을 수 있다.
 */
public class MySub implements Subscriber<Integer> {

    private Subscription subscription;
    private final int BUFFER_SIZE = 3;
    private int cnt = BUFFER_SIZE;
    int day = 1;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        System.out.printf("[구독자] : 저는 하루에 \"%d\"개 씩 추천해 주세요 !\n", BUFFER_SIZE);
        subscription.request(BUFFER_SIZE);
    }

    /**
     *  남아 있는 동영상을 추천한다. 하루 최대치를 모두 추천했다면 day를 증가시킨다. (그 날은 더이상 추천을 안한다는 것을 표현)
     *  하루가 지나서야 다음날의 동영상을 추천해준다.
     */
    @Override
    public void onNext(Integer integer) {

        if (cnt == BUFFER_SIZE)
            System.out.printf("=============== %d일차 ===============\n", day);
        System.out.printf("[No.%d] ", integer);

        cnt--;
        if (cnt == 0) {
            System.out.println("를 추천 받았습니다.");

            try {
                String z = "z";
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(700);
                    System.out.println(z);
                    z += "Z";
                }
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            day++;
            cnt = BUFFER_SIZE;
            subscription.request(cnt);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("구독 중 에러");
    }

    @Override
    public void onComplete() {
        if (cnt < BUFFER_SIZE)
            System.out.println("를 추천 받았습니다.");
        System.out.println("========== 모든 영상 시청 완료 ==========");
    }
}
