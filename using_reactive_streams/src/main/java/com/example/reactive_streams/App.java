package com.example.reactive_streams;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class App {

    public static void main(String[] args) throws Exception {
        MyPub pub = new MyPub();
        MySub sub = new MySub();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // 1. 유튜버에 구독자 정보를 추가한다.
        System.out.println("해당 유튜버를 구독하면 매일 3개의 동영상을 추천 받을 수 있습니다. 구독하시겠습니까 ?(숫자로 입력 하세요)");
        System.out.println("1: Yes, 2: No");
        int answer = Integer.valueOf(br.readLine());

        if (answer == 1)
            pub.subscribe(sub);
        else
            System.out.println("프로그램을 종료합니다.");
    }
}
