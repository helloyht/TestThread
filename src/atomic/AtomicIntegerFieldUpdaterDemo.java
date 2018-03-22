package atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Created by rhwayfun on 16-4-5.
 */
public class AtomicIntegerFieldUpdaterDemo {

    //创建一个原子更新器
    private static AtomicIntegerFieldUpdater<User> atomicIntegerFieldUpdater =
            AtomicIntegerFieldUpdater.newUpdater(User.class,"old");

    public static void main(String[] args){
        User user = new User("Tom",15);
        //原来的年龄
        System.out.println(atomicIntegerFieldUpdater.getAndIncrement(user));
        //现在的年龄
        System.out.println(atomicIntegerFieldUpdater.get(user));
    }

    static class User{
        private String name;
        public volatile int old;

        public User(String name, int old) {
            this.name = name;
            this.old = old;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getOld() {
            return old;
        }

        public void setOld(int old) {
            this.old = old;
        }
    }
}