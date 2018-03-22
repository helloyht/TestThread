package Executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by rhwayfun on 16-4-4.
 * Executor框架包括线程池,Executor,Executors,ExecutorService,CompletionService,Future,C allable 等。
 * 主线程首先通过Runnable或者Callable接口创建任务对象。
 * 工具类Executors可以把一个Runnable对象封装为Callable对象（通过调用Executors.callable(Runnable task)实现），
 * 然后可以把Runnable对象直接交给ExecutorService执行，ExecutorService通过调用ExecutorService.execute(Runnable command)完成任务的执行；
 * 或者把Runnable对象或Callable对象交给ExecutorService执行，
 * ExecutorService通过调用ExecutorService.submit(Runnable task)或者ExecutorService.submit(Callable task)完成任务的提交。
 * 在使用ExecutorService的submit方法的时候会返回一个实现Future接口的对象（目前返回的是FutureTask对象）。
 * 由于FutureTask实现了Runnable，也可以直接创建FutureTask，然后交给ExecutorService执行。
 * ExecutorService 接口继承自 Executor 接口，它提供了更丰富的实现多线程的方法。比如可以调用 ExecutorService 的 shutdown()方法来平滑地关闭 ExecutorService，
 * 调用该方法后，将导致 ExecutorService 停止接受任何新的任务且等待已经提交的任务执行完成(已经提交的任务会分两类：一类是已经在执行的，另一类是还没有开始执行的)，
 * 当所有已经提交的任务执行完毕后将会关闭 ExecutorService。
 * 通过Executors工具类可以创建不同的线程池ThreadPoolExecutor：SingleThreadExecutor、FixedThreadPool和CachedThreadPool。
 */
public class ExecutorCallableTest {

    /**
     * Callable任务
     */
    static class Runner implements Callable<String> {

        private String runId;

        public Runner(String runId) {
            this.runId = runId;
        }

        public String call() throws Exception {
            System.out.println(Thread.currentThread().getName() + " call method is invoked!");
            return Thread.currentThread().getName() + " call method and id is " + runId;
        }
    }

    public static void main(String[] args) {
        //线程池
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        //接收Callable任务的返回结果
        List<Future<String>> futureTaskList = new ArrayList<Future<String>>();

        for (int i = 0; i < 5; i++) {
            Future<String> future = cachedThreadPool.submit(new Runner(String.valueOf(i)));
            futureTaskList.add(future);
        }

        //遍历线程执行的返回结果
        for (Future f : futureTaskList) {
            try {
                //如果任务没有完成则忙等待
                while (!f.isDone()) {}
                System.out.println(f.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
                //关闭线程池，不再接收新的任务
                cachedThreadPool.shutdown();
            }
        }
    }
}
