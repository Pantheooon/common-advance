package cn.pmj.common.flink.helloword.windowfunction;

import lombok.Data;
import lombok.ToString;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: Pantheon
 * @date: 2019/6/4 10:23
 * @comment:
 */
public class ProcessFunctionTest {

    public static void main(String[] args) throws Exception {
        long s = 1559751247007L;
        Date date = new Date(s);
        System.out.println(date);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置时间为eventTime
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        SingleOutputStreamOperator<OptLog> stream = env.addSource(new SimpleSourceFunction()).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<OptLog>() {
            @Override
            public long extractAscendingTimestamp(OptLog element) {
                return element.getOpTs();
            }
        });
        WindowedStream<OptLog, String, TimeWindow> window = stream.keyBy(new KeySelector<OptLog, String>() {

            @Override
            public String getKey(OptLog value) throws Exception {
                return value.getUserName();
            }
        }).timeWindow(Time.seconds(20));
        SingleOutputStreamOperator result = window.process(new ProcessWindowFunction<OptLog, OptLog, String, TimeWindow>() {
            @Override
            public void process(String s, Context context, Iterable<OptLog> elements, Collector<OptLog> out) throws Exception {
                for (OptLog element : elements) {
                    out.collect(element);
                }
            }
        });
        result.print();
        env.execute();
    }


    /**
     * 操作日志
     */
    @Data
    @ToString
    public static class OptLog {
        /**
         * 用户名
         */
        private String userName;
        /**
         * 操作类型
         */
        private int opType;
        /**
         * 时间戳
         */
        private long opTs;

        private String time;

        public OptLog(String userName, int opType, long opTs) {
            this.userName = userName;
            this.opType = opType;
            this.opTs = opTs;
            Date date = new Date(opTs);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            time = sdf.format(date);
        }

        public static OptLog of(String userName, int opType, long opTs) {
            return new OptLog(userName, opType, opTs);
        }

    }


    public static final String[] nameArray = new String[]{
            "张三",
            "李四",
            "王五",
            "赵六",
            "钱七"
    };

    private static class SimpleSourceFunction implements SourceFunction<OptLog> {

        private long num = 0L;
        private volatile boolean isRunning = true;

        @Override
        public void run(SourceContext<OptLog> sourceContext) throws Exception {
            while (isRunning) {
                int randomNum = (int) (1 + Math.random() * (5 - 1 + 1));
                sourceContext.collect(OptLog.of(nameArray[randomNum - 1], randomNum, System.currentTimeMillis()));
                num++;
                Thread.sleep(5000);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

    private static class CountWithTimeOutFunction extends ProcessFunction<OptLog, Tuple2<String, Long>> {


        private ValueState<CountWithTimeStamp> state;


        @Override
        public void open(Configuration parameters) throws Exception {
            state = getRuntimeContext().getState(new ValueStateDescriptor<CountWithTimeStamp>("mystate", CountWithTimeStamp.class));
        }

        @Override
        public void processElement(OptLog optLog, Context ctx, Collector<Tuple2<String, Long>> out) throws Exception {
            System.out.println("processElement-->" + optLog);
            CountWithTimeStamp current = state.value();
            if (current == null) {
                current = new CountWithTimeStamp();
                current.key = optLog.userName;
            }
            current.count++;
            current.lastModified = ctx.timestamp();
            state.update(current);
            ctx.timerService().registerEventTimeTimer(current.lastModified + 30000);
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<String, Long>> out) throws Exception {
            CountWithTimeStamp result = state.value();
            if (timestamp == result.lastModified + 30000) {
                out.collect(new Tuple2<String, Long>(result.key, result.count));
            }
        }
    }

    @Data
    private static class CountWithTimeStamp {
        public String key;
        public long count;
        public Long lastModified;
    }
}
