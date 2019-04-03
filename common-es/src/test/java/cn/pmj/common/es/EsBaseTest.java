package cn.pmj.common.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author: Pantheon
 * @date: 2019/4/3 14:27
 */
public abstract class EsBaseTest {

    protected TransportClient client;


    @Before
    public void before() {
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        client = new PreBuiltTransportClient(settings);
        InetSocketAddress address = new InetSocketAddress("localhost", 9300);
        client.addTransportAddress(new TransportAddress(address));
    }

    @After
    public void after() {
        if (Objects.nonNull(client)) {
            client.close();
        }
    }
}
