package LockSyncronized;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by rhwayfun on 16-4-5.
 */
public class SyncInterruptDemo {

    //锁对象
    private static Object lock = new Object();
    //日期格式器
    private static DateFormat format = new SimpleDateFormat("HH:mm:ss");

    /**
     * 写数据
     */
    public void write(){
        synchronized (lock){
            System.out.println(Thread.currentThread().getName() + ":start writing data at " + format.format(new Date()));
            long start = System.currentTimeMillis();
            for (;;){
                //写15秒的数据
                if (System.currentTimeMillis() - start > 1000 * 15){
                    break;
                }
            }
            //过了15秒才会运行到这里
            System.out.println(Thread.currentThread().getName() + ":finish writing data at " + format.format(new Date()));
        }
    }

    /**
     * 读数据
     */
    public void read(){
        synchronized (lock){
            System.out.println(Thread.currentThread().getName() + ":start reading data at "
                    + format.format(new Date()));
        }
    }

    /**
     * 执行写数据的线程
     */
    static class Writer implements Runnable{

        private SyncInterruptDemo syncInterruptDemo;

        public Writer(SyncInterruptDemo syncInterruptDemo) {
            this.syncInterruptDemo = syncInterruptDemo;
        }

        public void run() {
            syncInterruptDemo.write();
        }
    }

    /**
     * 执行读数据的线程
     */
    static class Reader implements Runnable{

        private SyncInterruptDemo syncInterruptDemo;

        public Reader(SyncInterruptDemo syncInterruptDemo) {
            this.syncInterruptDemo = syncInterruptDemo;
        }

        public void run() {
            syncInterruptDemo.read();
            System.out.println(Thread.currentThread().getName() + ":finish reading data at "
                    + format.format(new Date()));
        }
    }

    public static void main(String[] args) throws InterruptedException {

        SyncInterruptDemo syncInterruptDemo = new SyncInterruptDemo();

        Thread writer = new Thread(new Writer(syncInterruptDemo),"Writer");
        Thread reader = new Thread(new Reader(syncInterruptDemo),"Reader");

        writer.start();
        reader.start();

        //运行5秒，然后尝试中断读线程
        TimeUnit.SECONDS.sleep(5);
        System.out.println(reader.getName() +":I don't want to wait anymore at " + format.format(new Date()));
        //中断读的线程
        reader.interrupt();
    }
}
