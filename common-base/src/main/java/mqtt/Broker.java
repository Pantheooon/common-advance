package mqtt;

/**
 * @author: Pantheon
 * @date: 2019/9/24 18:20
 * @comment:
 */
public interface Broker<T> {

    /**
     * 接收发布
     */
    void receive(T t);


    /**
     * 接收订阅
     *
     * @param topic
     */
    void suscribed(String topic);
}
