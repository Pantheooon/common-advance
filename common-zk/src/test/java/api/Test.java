package api;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * @author: Pantheon
 * @date: 2019/5/10 16:17
 */
public class Test {


    public ZooKeeper zooKeeper;

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


    @After
    public void setDown() throws InterruptedException {
        zooKeeper.close();
    }


    String serviceId = Long.toString(new Random().nextLong());
    boolean isLeader = false;

    @org.junit.Test
    public void runForMaster()  {
        while (true){
            try {
                zooKeeper.create("/master", "1520".getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                isLeader = true;
            } catch (KeeperException e) {
                isLeader = false;
                break;
            } catch (InterruptedException e) {
                isLeader = false;
                break;
            }

            if (checkeMaster()){
                break;
            }
        }


    }


    private boolean checkeMaster() {
        while (true) {
            try {
                Stat stat = new Stat();
                byte[] data = zooKeeper.getData("/master", false, stat);
                isLeader = new String(data).equals(serviceId);
                return true;
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    @org.junit.Test
    public void getData() throws KeeperException, InterruptedException {

        assert serviceId.equals("1520");
    }
}
