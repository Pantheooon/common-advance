package cn.pmj.flink.datastream;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MultiDataStream {

    static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    @Test
    public void union() {
        DataStreamSource<Tuple2<String, Integer>> stream1 =
                env.fromElements(new Tuple2<>("a", 3), new Tuple2<>("d", 4), new Tuple2<>("c", 2), new Tuple2<>("c", 5), new Tuple2<>("a", 5));
        DataStreamSource<Tuple2<String, Integer>> stream2 =
                env.fromElements(new Tuple2<>("d", 1), new Tuple2<>("s", 2), new Tuple2<>("a", 4), new Tuple2<>("e", 5), new Tuple2<>("a", 6));
        DataStreamSource<Tuple2<String, Integer>> stream3 =
                env.fromElements(new Tuple2<>("a", 2), new Tuple2<>("d", 1), new Tuple2<>("s", 2), new Tuple2<>("c", 3), new Tuple2<>("b", 1));
        stream1.union(stream2, stream3);
    }


    @Test
    public void connect() {
        DataStreamSource<Tuple2<String, Integer>> stream1 =
                env.fromElements(new Tuple2<>("a", 3), new Tuple2<>("d", 4), new Tuple2<>("c", 2), new Tuple2<>("c", 5), new Tuple2<>("a", 5));
        DataStreamSource<Integer> stream2 = env.fromElements(1, 2, 3, 4, 5, 6);
        ConnectedStreams<Tuple2<String, Integer>, Integer> connect = stream1.connect(stream2);
        //通过keyBy关联,指定相同key的数据会路由到同一个算子
        connect.keyBy(1, 0);
        //broadCast是会将stream2的数据路由到所有算子中
        ConnectedStreams<Tuple2<String, Integer>, Integer> connect1 = stream1.connect(stream2.broadcast());
//        connect.map(new MyConMapFunction());
//        connect.flatMap(new MyCoFlatMap());
    }

    @Test
    public void testSplitAndSelect() throws Exception {
        //将流进行切分打标记
        DataStreamSource<Tuple2<String, Integer>> stream1 =
                env.fromElements(new Tuple2<>("a", 3), new Tuple2<>("d", 4), new Tuple2<>("c", 2), new Tuple2<>("c", 5), new Tuple2<>("a", 5));
        //筛选出来
        stream1.split(new MyOutputSelector()).select("odd").print();
        env.execute();
    }


    @Test
    public void testIterator() throws Exception {
        //输入一组数据，我们对他们分别进行减1运算，直到等于0为止
        DataStream<Long> input=env.generateSequence(0,100);

        //基于输入流构建IterativeStream(迭代头)
        IterativeStream<Long> itStream=input.iterate();
        //定义迭代逻辑(map fun等)
        DataStream<Long> minusOne=itStream.map(new MapFunction<Long, Long>() {

            @Override
            public Long map(Long value) throws Exception {
                return value-1;
            }
        });

        //定义反馈流逻辑(从迭代过的流中过滤出符合条件的元素组成的部分流反馈给迭代头进行重复计算的逻辑)
        DataStream<Long> greaterThanZero=minusOne.filter(new FilterFunction<Long>() {
            @Override
            public boolean filter(Long value) throws Exception {
                return value>0;
            }
        });

        //调用IterativeStream的closeWith方法可以关闭一个迭代（也可表述为定义了迭代尾）
        itStream.closeWith(greaterThanZero);

        //定义“终止迭代”的逻辑(符合条件的元素将被分发给下游而不用于进行下一次迭代)
        DataStream<Long> lessThanZero=minusOne.filter(new FilterFunction<Long>() {
            @Override
            public boolean filter(Long value) throws Exception {
                return value<=0;
            }
        });

        lessThanZero.print();

        env.execute("IterativeStreamJob");
    }


    public class MyConMapFunction implements CoMapFunction<Tuple2<String, Integer>, Integer, Tuple2<String, Integer>> {
        @Override
        public Tuple2<String, Integer> map1(Tuple2<String, Integer> value) throws Exception {
            return value;
        }

        @Override
        public Tuple2<String, Integer> map2(Integer value) throws Exception {
            return new Tuple2<>("default", value);
        }
    }

    public class MyCoFlatMap implements CoFlatMapFunction<Tuple2<String, Integer>, Integer, Integer> {
        @Override
        public void flatMap1(Tuple2<String, Integer> value, Collector<Integer> out) throws Exception {
            out.collect(value.f1);
        }

        @Override
        public void flatMap2(Integer value, Collector<Integer> out) throws Exception {
            out.collect(value);
        }
    }

    private class MyOutputSelector implements org.apache.flink.streaming.api.collector.selector.OutputSelector<Tuple2<String, Integer>> {
        @Override
        public Iterable<String> select(Tuple2<String, Integer> value) {
            List<String> list = new ArrayList<>();
            if (value.f1 % 2 == 0) {
                list.add("odd");
            } else {
                list.add("even");
            }

            return list;
        }
    }
}
