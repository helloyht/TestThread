package LockSyncronized;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by rhwayfun on 16-4-5.
 * 1 可响应中断的锁。当在等待锁的线程如果长期得不到锁，那么可以选择不继续等待而去处理其他事情，而synchronized的互斥锁则必须阻塞等待，不能被中断
 * 2 可实现公平锁。所谓公平锁指的是多个线程在等待锁的时候必须按照线程申请锁的时间排队等待，而非公平性锁则保证这点，每个线程都有获得锁的机会。synchronized的锁和ReentrantLock使用的默认锁都是非公平性锁，但是ReentrantLock支持公平性的锁，
 *    在构造函数中传入一个boolean变量指定为true实现的就是公平性锁。不过一般而言，使用非公平性锁的性能优于使用公平性锁
 * 3 每个synchronized只能支持绑定一个条件变量，这里的条件变量是指线程执行等待或者通知的条件，而ReentrantLock支持绑定多个条件变量，
 *    通过调用lock.newCondition()可获取多个条件变量。不过使用多少个条件变量需要依据具体情况确定。
 */
public class LockInterruptDemo {
    //锁对象
    private static Lock lock = new ReentrantLock();
    //日期格式器
    private static DateFormat format = new SimpleDateFormat("HH:mm:ss");

    /**
     * 写数据
     */
    public void write() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + ":start writing data at "
                    + format.format(new Date()));
            long start = System.currentTimeMillis();
            for (;;){
                if (System.currentTimeMillis() - start > 1000 * 15){
                    break;
                }
            }
            System.out.println(Thread.currentThread().getName() + ":finish writing data at "
                    + format.format(new Date()));
        }finally {
            lock.unlock();
        }
    }

    /**
     * 读数据
     */
    public void read() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            System.out.println(Thread.currentThread().getName() + ":start reading data at "
                    + format.format(new Date()));
        }finally {
            lock.unlock();
        }
    }

    /**
     * 执行写数据的线程
     */
    static class Writer implements Runnable {

        private LockInterruptDemo lockInterruptDemo;

        public Writer(LockInterruptDemo lockInterruptDemo) {
            this.lockInterruptDemo = lockInterruptDemo;
        }

        public void run() {
            lockInterruptDemo.write();
        }
    }

    /**
     * 执行读数据的线程
     */
    static class Reader implements Runnable {

        private LockInterruptDemo lockInterruptDemo;

        public Reader(LockInterruptDemo lockInterruptDemo) {
            this.lockInterruptDemo = lockInterruptDemo;
        }

        public void run() {
            try {
                lockInterruptDemo.read();
                System.out.println(Thread.currentThread().getName() + ":finish reading data at "
                        + format.format(new Date()));
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + ": interrupt reading data at "
                        + format.format(new Date()));
            }
            System.out.println(Thread.currentThread().getName() + ":end reading data at "
                    + format.format(new Date()));
        }
    }

    public static void main(String[] args) throws InterruptedException {

        LockInterruptDemo lockInterruptDemo = new LockInterruptDemo();

        Thread writer = new Thread(new Writer(lockInterruptDemo), "Writer");
        Thread reader = new Thread(new Reader(lockInterruptDemo), "Reader");

        writer.start();
        reader.start();

        //运行5秒，然后尝试中断
        TimeUnit.SECONDS.sleep(5);
        System.out.println(reader.getName() + ":I don't want to wait anymore at " + format.format(new Date()));
        //中断读的线程
        reader.interrupt();
    }

}
