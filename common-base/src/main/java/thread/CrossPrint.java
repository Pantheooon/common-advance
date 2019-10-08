package thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: Pantheon
 * @date: 2019/10/8 21:14
 * @doc 两个线程，轮流打印奇偶数
 */
public class CrossPrint {


    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        NumPrint print = new CasNumPrint();
        PrintThread evenPrint = new PrintThread(print);
        PrintThread oddPrint = new PrintThread(print);
        threadPool.execute(evenPrint);
        threadPool.execute(oddPrint);
    }


    static class CasNumPrint extends NumPrint {


        private ReentrantLock reentrantLock = new ReentrantLock();
        private Condition condition = reentrantLock.newCondition();

        @Override
        public void print() {
            try {
                reentrantLock.lock();
                while (num <= 100) {
                    condition.signalAll();
                    System.out.println(String.format("current Thread %s:%d", Thread.currentThread().getId(), num++));
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                reentrantLock.unlock();
            }

        }
    }


    static class PrintThread implements Runnable {
        private NumPrint numPrint;

        public PrintThread(NumPrint numPrint) {
            this.numPrint = numPrint;
        }

        @Override
        public void run() {
            numPrint.print();
        }
    }


    static class NumPrint {

        protected Integer num = 0;

        public synchronized void print() {
            while (num <= 100) {
                notify();
                System.out.println(String.format("current Thread %s:%d", Thread.currentThread().getId(), num++));
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
