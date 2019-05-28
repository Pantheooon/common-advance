package cn.pmj.flink.datastream;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.junit.Before;
import org.junit.Test;

public class TransformationApi {
    static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    DataStreamSource<Tuple2<Integer, Integer>> streamSource;

    @Before
    public void setUp(){
     streamSource = env.fromElements(new Tuple2<>(1, 2), new Tuple2<>(1, 3), new Tuple2<>(1, 4), new Tuple2<>(2, 5));
    }

    /**
     * map操作可以用来做数据清洗
     * 用來处理原有的数据产生新的数据流
     * @throws Exception
     */
    @Test
    public void  map() throws Exception {
        SingleOutputStreamOperator<Tuple2<Integer, Integer>> map = streamSource.map(new MyMapFunction());
//        似乎lambada加return的方式不太好指定
//        streamSource.returns();
        map.print();
        env.execute();
    }


    /**
     * 输入一个数据源,产生多个数据源
     * 场景,对文本进行切割计数
     */
    @Test
    public void testFlatMap() throws Exception {
        streamSource.flatMap(new MyFlatMapFunction()).print();
        env.execute();
    }


    @Test
    public void testFilter() throws Exception {
        streamSource.filter(new MyFilter()).print();
        env.execute();
    }

    /**
     * reduce操作,sum是也是一种特殊的reduce操作,就是对最后的结果进行操作
     * @throws Exception
     */
    @Test
    public void testReduce() throws Exception {
        streamSource.keyBy(0).reduce(new MyReduceFunction()).print();
        env.execute();
    }

    /**
     * 进行分区
     */
    @Test
    public void testKeyBy() throws Exception {
        streamSource.keyBy(0).print();
        env.execute();
    }

    /**
     * 聚合操作,min  minBy  max  maxBy sum
     * @throws Exception
     */
    @Test
    public void testMin() throws Exception {
        streamSource.keyBy(0).min(1).print();
        env.execute();
    }

    class MyFlatMapFunction implements FlatMapFunction<Tuple2<Integer,Integer>,Integer>{

        @Override
        public void flatMap(Tuple2<Integer, Integer> value, Collector<Integer> out) throws Exception {
            out.collect(value.f0);
        }
    }

    private class MyFilter implements FilterFunction<Tuple2<Integer, Integer>> {
        @Override
        public boolean filter(Tuple2<Integer, Integer> value) throws Exception {
            if (value.f0 == 1){
                return true;
            }
            return false;
        }
    }


    class MyMapFunction implements MapFunction<Tuple2<Integer,Integer>,Tuple2<Integer,Integer>> {
        @Override
        public Tuple2<Integer,Integer> map(Tuple2<Integer,Integer> value) throws Exception {
            value.f0+=1;
            return value;
        }
    }

    public class MyReduceFunction implements ReduceFunction<Tuple2<Integer, Integer>> {

        @Override
        public Tuple2<Integer, Integer> reduce(Tuple2<Integer, Integer> value1, Tuple2<Integer, Integer> value2) throws Exception {

            return Tuple2.of(value1.f0+value2.f0,value1.f1+value2.f1);
        }
    }
}
