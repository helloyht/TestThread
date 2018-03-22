import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by rhwayfun on 16-4-4.
 * 使用阻塞队列实现生产者消费者模式
 */
public class ProducerConsumerModeWithBlockQueueTest {

    static class Info{
        //内容
        private String content;

        public Info(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return this.getContent();
        }
    }

    static class Producer implements Runnable{

        private final BlockingQueue<Info> blockingQueue;

        public Producer(BlockingQueue<Info> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        public void run() {
            boolean flag = true;
            for (int i = 0; i < 5; i++){
                if (flag){
                    try {
                        blockingQueue.put(new Info("contentA"));
                        System.out.println("[生产者]：contentA");
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    flag = false;
                }else {
                    try {
                        blockingQueue.put(new Info("contentB"));
                        System.out.println("[生产者]：contentB");
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    flag = true;
                }
            }
        }
    }

    static class Consumer implements Runnable{

        private final BlockingQueue<Info> blockingQueue;

        public Consumer(BlockingQueue<Info> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        public void run() {
            while (true){
                try {
                    System.out.println("[消费者]：" + blockingQueue.take());
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args){
        BlockingQueue<Info> blockingQueue = new LinkedBlockingQueue<Info>();
        new Thread(new Producer(blockingQueue)).start();
        new Thread(new Consumer(blockingQueue)).start();
    }
}

//那么阻塞队列是如何实现线程的同步的呢？使用通知模式实现。
//通知模式是指当生产者往满的队列添加元素的时候会阻塞生产者，当消费者消费了一个队列中的元素后，会通知生产者当前队列已经不满了，
//这时生产者可以继续往队列中添加元素。就ArrayBlockingQueue而言，是使用Condition条件变量实现通知模式的。
//在Java中提供了7种阻塞队列，使用较多有四种：ArrayBlockingQueue、LinkedBlockingQueue、PriorityBlockingQueue和DelayQueue。
//ArrayBlockingQueue是一个由数组结构组成的有界阻塞队列，
//LinkedBlockingQueue是一个由链表结构组成的有界阻塞队列，PriorityBlockingQueue是一个支持优先级排序的无界阻塞队列，
//DelayQueue是一个使用优先级队列实现的支持延时获取元素的无界阻塞队列。DelayQueue适用于缓存系统的设计以及定时任务调度等场景。