package Executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rhwayfun on 16-4-4.
 */
public class ExecutorRunnableTest {

    static class Runner implements Runnable{
        public void run() {
            System.out.println(Thread.currentThread().getName() + " is called");
        }
    }

    public static void main(String[] args){
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++){
            cachedThreadPool.execute(new Runner());
        }
        cachedThreadPool.shutdown();
    }
}