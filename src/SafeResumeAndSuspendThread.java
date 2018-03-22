import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by rhwayfun on 16-4-2.
 */
public class SafeResumeAndSuspendThread {

    private static DateFormat format = new SimpleDateFormat("HH:mm:ss");
    //对象锁
    private static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Runner r = new Runner();
        Thread runThread = new Thread(r,"CountThread");
        runThread.start();

        //主线程休眠一会，让CountThread有机会执行
        TimeUnit.SECONDS.sleep(2);

        for (int i = 0; i < 3; i++){
            //让线程挂起
            r.suspendRequest();
            //让计数线程挂起两秒
            TimeUnit.SECONDS.sleep(2);
            //看看i的值
            System.out.println("after suspend, i = " + r.getValue());
            //恢复线程的执行
            r.resumeRequest();

            //线程休眠一会
            TimeUnit.SECONDS.sleep(1);
        }

        //退出程序
        System.exit(0);
    }

    /**
     * 该线程是一个计数线程
     */
    private static class Runner implements Runnable{
        //变量i
        private volatile long i;
        //是否继续运行的标志
        //这里使用volatile关键字可以保证多线程并发访问该变量的时候
        //其他线程都可以感知到该变量值的变化。这样所有线程都会从共享
        //内存中取值
        private volatile boolean suspendFlag;

        public void run() {
            try {
                suspendFlag = false;
                i = 0;
                work();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void work() throws InterruptedException {
            while (true){
                //只有当线程挂起的时候才会执行这段代码
                waitWhileSuspend();
                i++;
                System.out.println("calling work method, i = " + i);
                //只有当线程挂起的时候才会执行这段代码
                waitWhileSuspend();
                //休眠1秒
                TimeUnit.SECONDS.sleep(1);
            }
        }

        /**
         * 忙等待
         * @throws InterruptedException
         */
        private void waitWhileSuspend() throws InterruptedException {
            /*while (suspendFlag){
                TimeUnit.SECONDS.sleep(1);
            }*/
            /**
             * 等待通知的方式才是最佳选择
             */
            synchronized (lock){
                while (suspendFlag){
                    System.out.println(Thread.currentThread().getName() + " suspend at " + format.format(new Date()));
                    lock.wait();
                }
            }
        }

        //让线程终止的方法
        public void resumeRequest(){
            synchronized (lock){
                try {
                    suspendFlag = false;
                    System.out.print("after call resumeRequest method, i = " + getValue() + ". ");
                    lock.notifyAll();
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void suspendRequest(){
            suspendFlag = true;
            System.out.print("after call suspendRequest method, i = " + getValue() + ". ");
        }

        public long getValue(){
            return i;
        }
    }
}