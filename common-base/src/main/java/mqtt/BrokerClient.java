package mqtt;

import java.util.Collection;

/**
 * @author: Pantheon
 * @date: 2019/9/24 18:15
 * @comment:
 */
public interface BrokerClient<T> {



    /**
     * 订阅
     *
     * @param topic
     */
    Collection<T> subscribe(Collection<String> topic);

    /**
     * 发送消息
     *
     * @param t
     */
    void send(T t);
}
