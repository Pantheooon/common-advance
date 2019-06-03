package cn.pmj.flink.datastream.window_watermark;

import cn.pmj.flink.stream.StreamApi;
import lombok.Data;
import lombok.ToString;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WaterMark {


    LocalStreamEnvironment localEnvironment;

    DataStreamSource<Log> streamSource;

    //1559571000000  2019-06-03 22:10:00
    @Before
    public void setUp() {
        localEnvironment = StreamExecutionEnvironment.createLocalEnvironment();
        streamSource = localEnvironment.fromCollection(generateSource());
        localEnvironment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    }


    @Test
    public void test() throws Exception {
        WindowedStream<Log, String, TimeWindow> timeWindow =
                streamSource.assignTimestampsAndWatermarks(new MyBoundedOutOfOrdernessTimestampExtractor(Time.seconds(10))).
                        map(new MyMapFunction()).keyBy(new MyKeySelector()).timeWindow(Time.seconds(10));
        timeWindow.reduce(new MyReduceFunctionc());
        localEnvironment.execute();


    }


    public class MyBoundedOutOfOrdernessTimestampExtractor extends BoundedOutOfOrdernessTimestampExtractor<Log> {

        public MyBoundedOutOfOrdernessTimestampExtractor(Time maxOutOfOrderness) {
            super(maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Log log) {
            return log.getTimeStamp();
        }
    }

    public List<Log> generateSource() {
        long initTimeStamp = 1559571000000L;
        List<Log> logs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Log log = new Log();
            log.setTimeStamp(initTimeStamp + 10 * i);
            log.setUri("/uri/detail" + i);
            log.setSellerId(i % 3 + "");
            log.setStuId(i % 3 + "");
            logs.add(log);
        }

        return logs;
    }


    @Data
    @ToString
    public static class Log implements Serializable {

        private Long timeStamp;

        private String uri;

        private String sellerId;

        private String stuId;

    }

    public class MyMapFunction implements MapFunction<Log, Log> {
        @Override
        public Log map(Log value) throws Exception {
            System.out.println(value);
            return value;
        }
    }

    public class MyKeySelector implements KeySelector<Log, String> {
        @Override
        public String getKey(Log value) throws Exception {
            return value.getSellerId();
        }
    }

    public static class MyReduceFunctionc implements org.apache.flink.api.common.functions.ReduceFunction<Log> {
        @Override
        public Log reduce(Log value1, Log value2) throws Exception {
            System.out.println(value1);
            System.out.println(value2);
            return new Log();
        }
    }
}
