package cn.pmj.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.junit.Before;

/**
 * @author: Pantheon
 * @date: 2019/4/8 16:51
 */
@Slf4j
public class Test {
    private CuratorFramework client;

    @Before
    public void before() {
        String address = "192.168.244.128:2181";
        RetryPolicy policy = new RetryOneTime(10);
        client = CuratorFrameworkFactory.newClient(address, policy);
        client.start();
    }


    @org.junit.Test
    public void fluent() throws Exception {
        String s = client.create().withMode(CreateMode.PERSISTENT).forPath("/pmj", new byte[0]);
        System.out.println(s);
    }
}
