package cn.pmj;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * @author: Pantheon
 * @date: 2019/3/29 13:16
 */
@Slf4j
public class ApiTest {



    @Test
    public void testWatch() throws IOException, InterruptedException, KeeperException {
        Master master = new Master();
        master.startZK();
        master.createNode();
        master.getMasterData();
        master.createNodeAsync();
        Thread.sleep(500000);
        master.stopZK();
    }




    class Master implements Watcher{
        ZooKeeper zooKeeper;
        String hostPort = "192.168.249.134:2181";


        void startZK() throws IOException {
            zooKeeper = new ZooKeeper(hostPort,15000,this);
        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println("process-->"+watchedEvent);
        }

        void stopZK() throws InterruptedException {
            zooKeeper.close();
        }

        void createNode() throws KeeperException, InterruptedException {
            String serverId = Integer.toHexString(new Random().nextInt());
            String s = zooKeeper.create("/master", serverId.getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("createNode-->"+s);
        }

        void getMasterData() throws KeeperException, InterruptedException {
            Stat stat = new Stat();
            byte[] data = zooKeeper.getData("/master", true, stat);
            System.out.println(new String(data));
        }


        void createNodeAsync(){
            zooKeeper.create("/master2", "123".getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
                @Override
                public void processResult(int rc, String path, Object ctx, String name) {
                    System.out.println(String.format("rc:%d,path:%s,name:%s",rc,path,name));
                }
            },new Object());
        }
    }
}
