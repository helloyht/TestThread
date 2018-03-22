
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rhwayfun on 16-4-2.
 */

//其实使用wait、notify/notifyAll很简单，但是仍然需要注意以下几点：

//使用wait()、notify()和notifyAll()时需要首先对调用对象加锁
//调用wait()方法后，线程状态会从RUNNING变为WAITING，并将当线程加入到lock对象的等待队列中
//调用notify()或者notifyAll()方法后，等待在lock对象的等待队列的线程不会马上从wait()方法返回，必须要等到调用notify()或者notifyAll()方法的线程将lock锁释放，等待线程才有机会从等待队列返回。
//这里只是有机会，因为锁释放后，等待线程会出现竞争，只有竞争到该锁的线程才会从wait()方法返回，其他的线程只能继续等待
//notify()方法将等待队列中的一个线程移到lock对象的同步队列，notifyAll()方法则是将等待队列中所有线程移到lock对象的同步队列，被移动的线程的状态由WAITING变为BLOCKED
//wait()方法上等待锁，可以通过wait(long timeout)设置等待的超时时间

public class WaitNotifyThread {

    //条件是否满足的标志
    private static boolean flag = true;
    //对象的监视器锁
    private static Object lock = new Object();
    //日期格式化器
    private static DateFormat format = new SimpleDateFormat("HH:mm:ss");

    public static void main(String[] args) throws InterruptedException{
        Thread waitThread = new Thread(new WaitThread(),"WaitThread");
        waitThread.start();
        Thread.sleep(1000);
        Thread notifyThread = new Thread(new NotifyThread(),"NotifyThread");
        notifyThread.start();
    }

    /**
     * 等待线程
     */
    private static class WaitThread implements Runnable{
        public void run() {
            //加锁，持有对象的监视器锁
            synchronized (lock){
                //只有成功获取对象的监视器才能进入这里
                //当条件不满足的时候，继续wait，直到某个线程执行了通知
                //并且释放了lock的监视器（简单来说就是锁）才能从wait
                //方法返回
                while (flag){
                    try {
                        System.out.println(Thread.currentThread().getName() + " flag is true,waiting at "
                                + format.format(new Date()));
                        lock.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //条件满足，继续工作
                System.out.println(Thread.currentThread().getName() + " flag is false,running at "
                        + format.format(new Date()));
            }
        }
    }

    /**
     * 通知线程
     */
    private static class NotifyThread implements Runnable{
        public void run() {
            synchronized (lock){
                //获取lock锁，然后执行通知，通知的时候不会释放lock锁
                //只有当前线程退出了lock后，waitThread才有可能从wait返回
                System.out.println(Thread.currentThread().getName() + " holds lock. Notify waitThread at "
                        + format.format(new Date()));
                lock.notifyAll();
                flag = false;
                try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            //再次加锁
            synchronized (lock){
                System.out.println(Thread.currentThread().getName() + " holds lock again. NotifyThread will sleep at "
                        + format.format(new Date()));
                try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
}