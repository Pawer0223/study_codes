package pureJava;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  Iterable < ------ > Observable 둘은 쌍대성(duality)의 관계를 가진다.
 *  궁극정 기능은 같지만, 반대 방향으로 표현
 *
 *  Iterable
 *      Pull 방식 -> 다음 꺼 줘 ! -> next()
 *
 *  Observable
 *      Push 방식 -> 이거 가져가라 ! -> notifyObservers();
 *
 *   Source 라고 생각 하자.
 *   Source -> Event(Data) -> Observer(관찰자) ->
 *   뭔가 새로운 정보가 생길때 마다 Observer 한테 넘겨준다.
 *
 *   Observer 가 여러개 될 수 있는게 Observable 의 특징.
 *
 *   Observer 패턴은 모든 옵저버한테 한번에 보내는, multicast 기능이 가능하다.
 *
 *   Observer 패턴에 없는 2가지 기능
 *
 *      1. Complete, 끝(완료)를 구분할 수 있는 방법이 없다.
 *      2. Error, 예외 발생 시 회복이 어렵다.
 *
 *   2가지가 추가 된 Observer 패턴이 Reactive Programming 의 한 축이다.
 *
 */

public class UnderstandObservable {

    // subject = publisher = 데이터를 만드는 source
    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for(int i = 1; i < 10; i++) {
                setChanged(); // 새로운 변화를 생겼음을 호출
                /**
                 *  ** 아래 코드(주석 까지) 중요
                 *
                 * Iterator 와 Observer 의 쌍대성을 나타낸다. * 방향이 완전히 반대이다.
                 *
                 *  notifyObservers
                 *      push    => 파라미터로 데이터를 넘김
                 *  next()
                 *      pull    => 데이터를 반환 함.
                 *
                 */
                notifyObservers(i); // 데이터를 전달, update 에서 받게 됨.
                // int i = iterator.next();
            }
        }
    }
    // observer = subscriber = 데이터를 받는
    public static void main(String[] args) {
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Integer v = (Integer)arg;
                System.out.println(Thread.currentThread().getName() + " " + -v);
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob); // 이때부터 IntObservable 이 던지는 모든 이벤트는 add 된 observer 들이 받게 된다.

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io); // 새로운 쓰레드를 할당해서, 별도의 쓰레드로 동작하도록 한다.

        // 쓰레드 안에서 이벤트를 발생시켜셔, 그 안에서 이벤트의 처리를 하도록 만들었다.
        // 별개의 쓰레드에서 동작하는 코드를 손쉽게 만들 수 있다. (push 방식의 observer 패턴을 통해)
        System.out.println(Thread.currentThread().getName() + " EXIT");
        es.shutdown();
    }
}
