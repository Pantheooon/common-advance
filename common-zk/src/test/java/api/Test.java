package api;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author: Pantheon
 * @date: 2019/5/10 16:17
 */
public class Test {


    private ZooKeeper zooKeeper;

    private String address = "127.0.0.1:2181";
    //等待客户端最长的时间
    private int sessionTimeOut = 1000 * 60 * 2;

    @Before
    public void setUp() throws IOException {
        zooKeeper = new ZooKeeper(address, sessionTimeOut, (event) -> {
            System.out.println(event);
        });
    }

    @org.junit.Test
    public void test() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1000);
        System.out.println(1);
    }


}
