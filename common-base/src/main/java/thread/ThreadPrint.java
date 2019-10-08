package thread;

import sun.misc.Unsafe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: Pantheon
 * @date: 2019/10/8 21:55
 * @doc 三个线程轮流打印abc
 */
public class ThreadPrint {


    private static ReentrantLock lock = new ReentrantLock();

    private static String flag = "A";

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.execute(new PrintA());
        executorService.execute(new PrintB());
        executorService.execute(new PrintC());
    }


    static class PrintA implements Runnable {

        @Override
        public void run() {
            while (true){
                lock.lock();
                if (flag.equals("A")){
                    System.out.println("A");
                    flag = "B";
                }
                lock.unlock();
            }

        }
    }


    static class PrintB implements Runnable {
        @Override
        public void run() {
            while (true) {
                lock.lock();
                if (flag.equals("B")) {
                    System.out.println("B");
                    flag = "C";
                }
                lock.unlock();
            }
        }
    }


    static class PrintC implements Runnable {
        @Override
        public void run() {
            while (true) {
                lock.lock();
                if (flag.equals("C")) {
                    System.out.println("C");
                    flag = "A";
                }
                lock.unlock();
            }
        }
    }


}
