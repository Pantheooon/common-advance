package cn.pmj.common.flink.helloword.windowfunction;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

import java.util.ArrayList;
import java.util.List;

public class LateDataStream {


    static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    private static List<Tuple4<String, String, Long,Long>> generateSource() {
        List<Tuple4<String, String, Long,Long>> list = new ArrayList<>();
        list.add(Tuple4.of("class1", "张三", 100L,System.currentTimeMillis()));
        list.add(Tuple4.of("class1", "李四", 78L,System.currentTimeMillis()));
        list.add(Tuple4.of("class1", "王五", 99L,System.currentTimeMillis()));
        list.add(Tuple4.of("class2", "赵六", 81L,System.currentTimeMillis()));
        list.add(Tuple4.of("class2", "钱七", 59L,System.currentTimeMillis()));
        list.add(Tuple4.of("class2", "马二", 97L,System.currentTimeMillis()));
        return list;
    }

    public static void main(String[] args) {
        OutputTag late = new OutputTag("late", TypeInformation.of(Tuple4.class));
        SingleOutputStreamOperator<Tuple4<String, String, Long, Long>> f3 = env.fromCollection(generateSource())
                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Tuple4<String, String, Long, Long>>() {
                    @Override
                    public long extractAscendingTimestamp(Tuple4<String, String, Long, Long> element) {
                        return element.f3;
                    }
                }).keyBy("f3").timeWindow(Time.seconds(10)).reduce(null);
        //延迟数据
        DataStream sideOutput = f3.getSideOutput(late);
    }

}
