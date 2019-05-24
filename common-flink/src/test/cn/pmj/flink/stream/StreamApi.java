package cn.pmj.flink.stream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class StreamApi {

    static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    static {
        env.setParallelism(1);
    }

    @Test
    public void testDemo() throws Exception {


        DataStreamSource<String> stream = env.readTextFile("D:\\pantheon\\code\\common-advance\\common-flink\\src\\main\\resources\\test");
        SingleOutputStreamOperator<Tuple2<String,Integer>> sum = stream.flatMap(new Tokenizer()).keyBy(0).sum(1);
        sum.print();
        env.execute();
    }

    @Test
    public void testFlatMap() throws Exception {
        List<String> strings = Arrays.asList("pmj1", "pmj2", "pmj3", "pmj2");
        DataStreamSource<String> streamSource = env.fromCollection(strings);
        SingleOutputStreamOperator<Tuple2<String, Integer>> flatMap = streamSource.flatMap(new Tokenizer());
        flatMap.print();
        env.execute();
    }

    @Test
    public void testMap() throws Exception {
        List<String> strings = Arrays.asList("pmj1", "pmj2", "pmj3", "pmj2");
        DataStreamSource<String> streamSource = env.fromCollection(strings);
        SingleOutputStreamOperator<String> map = streamSource.map(new MyMapFunction());
        map.print();
        env.execute();
    }


    @Test
    public void testMapAndFlatMap() throws Exception {
        List<String> strings = Arrays.asList("pmj1", "pmj2", "pmj3", "pmj2");
        DataStreamSource<String> streamSource = env.fromCollection(strings);
        SingleOutputStreamOperator<Tuple2<String, Integer>> tuple2TupleKeyedStream = streamSource.map(new MyMapFunction())
                .flatMap(new Tokenizer()).keyBy(0).sum(1);

        tuple2TupleKeyedStream.print();
        env.execute();
    }

    public class MyMapFunction implements MapFunction<String,String> {
        @Override
        public String map(String value) throws Exception {
            return value+"sb";
        }
    }


    public class HH<T>{
        private T t;

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }

    public class MyReduceFunction implements ReduceFunction<Tuple2<String, Integer>>{
        @Override
        public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
            return new Tuple2(value1.f0,value1.f1+value1.f1);
        }
    }


    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }

}
