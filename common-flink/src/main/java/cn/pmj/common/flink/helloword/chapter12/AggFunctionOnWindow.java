package cn.pmj.common.flink.helloword.chapter12;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamContextEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Pantheon
 * @date: 2019/6/3 10:42
 * @comment:
 */
public class AggFunctionOnWindow {


    private static List<Tuple3<String, String, Long>> generateSource() {
        List<Tuple3<String, String, Long>> list = new ArrayList<>();
        list.add(Tuple3.of("class1", "张三", 100L));
        list.add(Tuple3.of("class1", "李四", 78L));
        list.add(Tuple3.of("class1", "王五", 99L));
        list.add(Tuple3.of("class2", "赵六", 81L));
        list.add(Tuple3.of("class2", "钱七", 59L));
        list.add(Tuple3.of("class2", "马二", 97L));
        return list;
    }


    public static void main(String[] args) throws Exception {
        LocalStreamEnvironment env = StreamContextEnvironment.createLocalEnvironment();
        //AverageAggreate
//        SingleOutputStreamOperator<Double> aggregate = env.fromCollection(generateSource()).keyBy(0)
//                .countWindow(3).aggregate(new AverageAggreate());

        //process
//        SingleOutputStreamOperator<Double> aggregate =
//                env.fromCollection(generateSource()).keyBy(0).countWindow(3).process(new MyProcessWindowFunction());
        //reduce
       SingleOutputStreamOperator<Tuple3<String, String, Long>> aggregate = env.fromCollection(generateSource()).keyBy(0).countWindow(2).reduce(new MyReduceFunction());

        aggregate.print();
        env.execute();
    }

    private static class AverageAggreate implements AggregateFunction<Tuple3<String, String, Long>, Tuple2<Long, Long>, Double> {
        @Override
        public Tuple2<Long, Long> createAccumulator() {
            return new Tuple2<>(0L, 0L);
        }

        @Override
        public Tuple2<Long, Long> add(Tuple3<String, String, Long> value, Tuple2<Long, Long> accumulator) {
            return new Tuple2<>(accumulator.f0 + value.f2, accumulator.f1 + 1L);
        }

        @Override
        public Double getResult(Tuple2<Long, Long> accumulator) {
            return ((double) accumulator.f0) / accumulator.f1;
        }

        @Override
        public Tuple2<Long, Long> merge(Tuple2<Long, Long> a, Tuple2<Long, Long> b) {
            return new Tuple2<>(a.f0 + b.f0, a.f1 + b.f1);
        }
    }

    private static class MyProcessWindowFunction extends ProcessWindowFunction<Tuple3<String, String, Long>, Double, Tuple, GlobalWindow> {
        @Override
        public void process(Tuple tuple, Context context, Iterable<Tuple3<String, String, Long>> elements, Collector<Double> out) throws Exception {
            double sum = 0L;
            Long count = 0L;
            for (Tuple3<String, String, Long> element : elements) {
                sum += element.f2;
                count++;
            }
            out.collect(sum / count);
        }
    }

    private static class MyReduceFunction implements org.apache.flink.api.common.functions.ReduceFunction<Tuple3<String, String, Long>> {
        @Override
        public Tuple3<String, String, Long> reduce(Tuple3<String, String, Long> value1, Tuple3<String, String, Long> value2) throws Exception {
            return new Tuple3<>(value1.f0, value1.f1, value1.f2 + value2.f2);
        }
    }
}
