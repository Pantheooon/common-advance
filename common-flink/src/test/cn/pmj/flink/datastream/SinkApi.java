package cn.pmj.flink.datastream;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.junit.Test;

public class SinkApi {

    static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    @Test
    public void testBase() throws Exception {
        DataStreamSource<Tuple2<String, Integer>> dataStreamSource = env.fromElements(new Tuple2<>("Alex", 18), new Tuple2<>("Peter", 43));
        dataStreamSource.writeAsCsv("d://person.csv", FileSystem.WriteMode.OVERWRITE);
        dataStreamSource.writeAsText("d://perosn.txt", FileSystem.WriteMode.OVERWRITE);
        SimpleStringSchema simpleStringSchema = new SimpleStringSchema();
//        dataStreamSource.writeToSocket("localhost",8080,simpleStringSchema);
        env.execute();
    }

    /**
     * kfaka
     */
    @Test
    public void thirdPartyTest(){
        DataStreamSource<Tuple2<String, Integer>> dataStreamSource = env.fromElements(new Tuple2<>("Alex", 18), new Tuple2<>("Peter", 43));
        FlinkKafkaProducer010<Tuple2<String,Integer>> producer = new FlinkKafkaProducer010("localhost:9092", "topic", new SimpleStringSchema());
        dataStreamSource.addSink(producer);
    }
}
