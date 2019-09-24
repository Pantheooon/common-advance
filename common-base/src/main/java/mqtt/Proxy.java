package mqtt;

import com.sun.corba.se.pept.broker.Broker;

import java.util.Collection;

/**
 * @author: Pantheon
 * @date: 2019/9/24 18:15
 * @comment:
 */
public interface Proxy extends mqtt.Broker, BrokerClient {

    /**
     * proxy保存的broker
     *
     * @return
     */
    Collection<Broker> getBrokers();

    /**
     * 连接
     *
     * @param clientId
     */
    void connect(String clientId);

    /**
     * 转发
     *
     * @param clientId
     */
    void dispatch(String clientId);


    /**
     * 根据clientId查找client
     *
     * @param clientId
     * @return
     */
    Session findClient(String clientId);


    /**
     * 处理心跳
     *
     * @param client
     */
    void heartBeat(String client);
}
