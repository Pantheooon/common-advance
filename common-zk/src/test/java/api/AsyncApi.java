package api;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.zookeeper.KeeperException.Code.CONNECTIONLOSS;
import static org.apache.zookeeper.KeeperException.Code.NONODE;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * @author: Pantheon
 * @date: 2019/5/15 16:32
 */
public class AsyncApi extends Test {


    private boolean isLeader;
    AsyncCallback.StringCallback callback = new AsyncCallback.StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            if (ctx instanceof Map) {
                Map map = (Map) ctx;
                System.out.println(map.get("aaa"));
            }
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    checkMaster();
                    return;
                case OK:
                    isLeader = true;
                    break;
                default:
                    isLeader = false;
            }
            System.err.println("I'm " + (isLeader ? " " : "not ") + "the leader");
        }
    };

    AsyncCallback.DataCallback dataCallback = (rc, path, ctx, data, stat) -> {
        switch (KeeperException.Code.get(rc)) {
            case CONNECTIONLOSS:
                checkMaster();
                return;
            case NONODE:
                runForMaster();
                return;
        }
    };


    private void checkMaster() {
        zooKeeper.getData("/master", false, dataCallback, null);
    }

    @org.junit.Test
    @Override
    public void runForMaster() {
        HashMap<Object, Object> context = new HashMap<>();
        context.put("aaa", "bbb");

        zooKeeper.create("/master", serviceId.getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, callback, context);
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
