package atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by rhwayfun on 16-4-5.
 */
public class AtomicIntegerArrayDemo {

static int[] value = new int[]{1, 2};

static AtomicIntegerArray ai = new AtomicIntegerArray(value);

public static void main(String[] args){
    ai.getAndSet(0,3);
    System.out.println(ai.get(0));
    System.out.println(value[0]);
}
}