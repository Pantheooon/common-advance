package cn.pmj.flink.datastream.window_watermark;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer08;
import org.junit.Test;
import scala.Tuple3;

import javax.annotation.Nullable;
import java.util.Map;

public class TimeApi {

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    DataStreamSource<Long> dataStreamSource ;
    {
        DataStreamSource<Long> dataStreamSource = env.generateSequence(0, 100);

    }

    @Test
    public void testTimeType(){

        //进入flink的时间
//        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
        //操作时间
//        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        //event time 事件本身产生的时间
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    }

    //sourceFunction中指定
    //AssignerWithPeriodicWatermarks 时间间隔周期性的生成watermark
    //AscendingTimestampExtractor 根据指定字段提取timestamp,并用当前的timestamp作为新的watermark,比较适合于事件按顺序生成,没有乱序的情况
//    BoundedOutOfOrdernessTimestampExtractor 设定固定的时间间隔来指定watermark落后于timestamp的区间长度,也就是最长容忍到多长时间内的数据到达系统
    //AssignerWithPunctuatedWatermarks 根据接入数据的数量生成
    @Test
    public void testTimeStampsAndWaterMark(){
        //在sourceFunction中指定
//        FlinkKafkaConsumer08<Map<String,Long>> sourceFunction = new FlinkKafkaConsumer08<>(null, null, null, null);
//        //AssignerWithPeriodicWatermarks
//        sourceFunction.assignTimestampsAndWatermarks(new MyAscendingTimestampExtractor());
//        //代表允許延迟10s
//        sourceFunction.assignTimestampsAndWatermarks(new MyBoundedOutOfOrdernessTimestampExtractor(Time.seconds(10)));
//        env.addSource(sourceFunction);

    }


    //适用于顺序的
    private class MyAscendingTimestampExtractor extends AscendingTimestampExtractor<Map<String,Long>> {



        @Override
        public long extractAscendingTimestamp(Map<String,Long> element) {
            return element.get("timestamp");
        }
    }

    private class MyBoundedOutOfOrdernessTimestampExtractor extends BoundedOutOfOrdernessTimestampExtractor<Map<String, Long>> {

        public MyBoundedOutOfOrdernessTimestampExtractor(Time maxOutOfOrderness) {
            super(maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Map<String, Long> element) {
            return element.get("timestamp");
        }
    }


    private class CustomeAssignerWithPeriodicWatermarks implements AssignerWithPeriodicWatermarks<Tuple3<String,Long,Integer>> {
        //1秒时延设定,表示在1秒以内的数据延时有效,超过1s的数据认为超时
        int maxOutOfOrderness= 1000;
        long currentMaxTimeStamp;
        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimeStamp - maxOutOfOrderness);
        }


        @Override
        public long extractTimestamp(Tuple3<String, Long, Integer> element, long previousElementTimestamp) {
            long currentTimeStamp = element._2();
            currentMaxTimeStamp = Math.max(currentTimeStamp,currentMaxTimeStamp);
            return currentTimeStamp;
        }
    }

    /**
     * 根据特殊条件生成watermark
     */
    private class CustomeAssignerWithPunctuatedWatermarks implements AssignerWithPunctuatedWatermarks<Tuple3<String,Long,Integer>>{
        @Nullable
        @Override
        public Watermark checkAndGetNextWatermark(Tuple3<String, Long, Integer> lastElement, long extractedTimestamp) {
            if (lastElement._3() == 0){
                return new Watermark(extractedTimestamp);
            }
            return null;
        }

        @Override
        public long extractTimestamp(Tuple3<String, Long, Integer> element, long previousElementTimestamp) {
            return element._2();
        }
    }
}
