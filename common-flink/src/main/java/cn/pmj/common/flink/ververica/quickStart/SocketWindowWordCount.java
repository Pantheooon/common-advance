package cn.pmj.common.flink.ververica.quickStart;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * @author: Pantheon
 * @date: 2019/5/21 11:30
 * https://zh.ververica.com/developers/build-from-zero/
 */
public class SocketWindowWordCount {

    public static void main(String[] args) throws Exception {
        //流式处理的环境
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        //监听9000端口
        DataStreamSource<String> text = environment.socketTextStream("localhost", 9000, "\n");
        DataStream<Tuple2<String, Integer>> windowCounts = text
                .flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
                        for (String word : value.split("\\s")) {
                            out.collect(Tuple2.of(word, 1));
                        }
                    }
                })
                .keyBy(0)
                .timeWindow(Time.seconds(5))
                .sum(1);
//
        windowCounts.print().setParallelism(1);
        environment.execute("socket window count");
    }

}
