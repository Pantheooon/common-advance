package cn.pmj.flink.datastream;

import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Test;

public class PartitionApi {
    static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    @Test
    public void test(){
        DataStreamSource<Long> streamSource = env.generateSequence(0, 100);
        //数据的随机分区
        DataStream<Long> shuffle =streamSource.shuffle();
        //Roundrobin
        streamSource.rebalance();
        //rescale
        streamSource.rescale();
        //braodcast
        streamSource.broadcast();
        //自定义分区
        streamSource.partitionCustom(new MyPartition(),0);
    }

    private class MyPartition implements Partitioner<Integer> {
        @Override
        public int partition(Integer key, int numPartitions) {
            return key % numPartitions;
        }
    }
}
