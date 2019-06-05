package cn.pmj.common.flink.helloword.state;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class KeyedState {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Tuple2<Long, Long>> streamSource = env.fromElements(Tuple2.of(1L, 4L),
                Tuple2.of(2L, 3L),
                Tuple2.of(3L, 1L),
                Tuple2.of(1L, 2L),
                Tuple2.of(3L, 2L),
                Tuple2.of(1L, 2L),
                Tuple2.of(2L, 2L),
                Tuple2.of(2L, 9L));
        streamSource.keyBy(0).flatMap(new CountWithKeyedState()).setParallelism(10).print();
        env.execute();
    }

    private static class CountWithKeyedState extends RichFlatMapFunction<Tuple2<Long,Long>,Tuple2<Long,Long>> {

        private transient ValueState<Tuple2<Long,Long>> sum;
        @Override
        public void flatMap(Tuple2<Long, Long> value, Collector<Tuple2<Long,Long>> out) throws Exception {
            Tuple2<Long, Long> value1 = sum.value();
            if (value1 == null){
                value1 = new Tuple2<>(0L,0L);
            }
            value1.f0 +=1;
            value1.f1+=value.f1;
            sum.update(value1);
            if (value1.f0>=3){
                out.collect(Tuple2.of(value.f0,value1.f1/value1.f0));
                sum.clear();
            }
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            ValueStateDescriptor<Tuple2<Long, Long>> avgState = new ValueStateDescriptor<>("avgState", TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
            }));
            sum = getRuntimeContext().getState(avgState);
        }
    }
}
