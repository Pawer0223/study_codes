package pureJava;

import java.util.Iterator;

/**
 *  Java 의 for-each 구문은 collection 이 아닌, Iterable 을 구현한 구현체로 동작한다.
 *
 * [ Iterable 특징 ]
 *  왜 iterator 다시 구현하는가 ?
 *      => Iterable 이 제공해주는 정보를 여러 client 에게 반복적으로 제공할 수 있도록. (*중요)
 */
public class UnderstandIterable {

    // basic
    Iterable<Integer> iter = new Iterable<Integer>() {
        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<>() {

                int i = 0;
                final int MAX = 10;

                public boolean hasNext() {
                    return i < MAX;
                }
                public Integer next() {
                    return ++i;
                }
            };
        }
    };

    // using Lamda
    Iterable<Integer> iterLamda = () ->
        new Iterator<>() {
            int i = 0;
            final int MAX = 10;

            public boolean hasNext() {
                return i < MAX;
            }
            public Integer next() {
                return ++i;
            }
        };

    public static void main(String[] args) {

        UnderstandIterable data = new UnderstandIterable();

        System.out.println(" === using my iterator 1 ===");
        Iterator itr = data.iterLamda.iterator();
        while (itr.hasNext()) {
            System.out.print(itr.next() + " ");
        }

        System.out.println();
        System.out.println(" === using my iterator 2 ===");
        for (Integer i : data.iterLamda) {
            System.out.print(i + "  ");
        }

    }
}
