package cn.pmj.common.flink.helloword.state;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public class StatefulFunction {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Tuple2<Integer, Long>> streamSource =
                env.fromElements(new Tuple2<Integer, Long>(2, 21L),
                        new Tuple2<Integer, Long>(2, 1L),
                        new Tuple2<Integer, Long>(2, 4L));
        streamSource.keyBy("f0").flatMap(new RichFlatMapFunction<Tuple2<Integer, Long>, Long>() {
            private ValueState<Long> valueState;

            @Override
            public void open(Configuration parameters) throws Exception {
                //state 生命周期
                //设置时间
                StateTtlConfig build = StateTtlConfig.newBuilder(Time.seconds(10))
                        //指定ttl刷新时只对创建和写入操作有效
                        .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                        //指定状态可见性为永远不返回过期数据
                        .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired).build();

                ValueStateDescriptor<Long> valueStateDescriptor = new ValueStateDescriptor<Long>("valueState", TypeInformation.of(new TypeHint<Long>() {
                }));
                valueStateDescriptor.enableTimeToLive(build);
                valueState = getRuntimeContext().getState(valueStateDescriptor);
            }

            @Override
            public void flatMap(Tuple2<Integer, Long> value, Collector<Long> out) throws Exception {
                Long leastVaule = valueState.value();
                if (leastVaule == null){
                    leastVaule = value.f1;
                }
                if (value.f1 < leastVaule){
                    out.collect(value.f1);
                }else {
                    valueState.update(value.f1);
                }
            }
        }).process(new ProcessFunction<Long, Object>() {
            @Override
            public void processElement(Long value, Context ctx, Collector<Object> out) throws Exception {
                System.out.println(value);
            }
        });
        env.execute();
    }
}
