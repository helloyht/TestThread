import java.util.concurrent.TimeUnit;


/**
 * Created by rhwayfun on 16-4-4.
 */
public class ProducerConsumerModeTest {
    public static void main(String[] args){
        Content content = new Content();
        Thread producer = new Thread(new Producer(content),"Producer");
        Thread consumer = new Thread(new Consumer(content),"Consumer");

        producer.start();
        consumer.start();
    }
    
}

/**
 * Created by rhwayfun on 16-4-4.
 */
class Consumer implements Runnable {

    private Content content;

    public Consumer(Content content) {
        this.content = content;
    }

    public void run() {
        for (int i = 0; i < 6; i++){
            try {
                content.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Producer implements Runnable {

    private Content content;

    public Producer(Content content) {
        this.content = content;
    }

    public void run() {
        boolean flag = true;
        for (int i = 0; i < 6; i++){
            if (flag){
                try {
                    content.set("authorA","titleA");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                flag = false;
            }else {
                try {
                    content.set("authorB","titleB");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                flag = true;
            }
        }
    }
}
    
/**
 * Created by rhwayfun on 16-4-4.
 */
class Content {

    //标题
    private String title;
    //作者
    private String author;

    //是否开始生产的标志，默认开始生产
    private boolean produce = true;

    /**
     * 消费者执行的方法
     * @param author
     * @param title
     * @throws InterruptedException
     */
    public synchronized void set(String author, String title) throws InterruptedException {
        //如果没有开始生产就阻塞等待
        while (!produce) {
            super.wait();
        }
        //设置作者
        this.setAuthor(author);
        //休眠1秒
        TimeUnit.SECONDS.sleep(1);
        //设置标题
        this.setTitle(title);
        System.out.println("[生产者]:" + this.getAuthor() + " --> " + this.getTitle());
        //设置标志位为false。表示可以取数据了
        produce = false;
        //唤醒正在等待的线程
        super.notify();
    }

    /**
     * 消费者执行的方法
     * @throws InterruptedException
     */
    public synchronized void get() throws InterruptedException {
        //如果已经开始生产了就阻塞等待
        while (produce){
            super.wait();
        }
        System.out.println("[消费者]:" + this.getAuthor() + " --> " + this.getTitle());
        //让生产者继续生产
        produce = true;
        super.notify();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}