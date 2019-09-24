package cn.pmj.flink.dataset;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapPartitionFunction;
import org.apache.flink.api.common.functions.RichMapPartitionFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.aggregation.Aggregations;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.JoinOperator;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.util.ArrayList;

public class OperationTest {

    final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    @Test
    public void testGroupBy() throws Exception {
        DataSet<Tuple3<Long, String, Integer>> inputs = env.fromElements(
                Tuple3.of(1L, "zhangsan", 28),
                Tuple3.of(3L, "lisi", 34),
                Tuple3.of(3L, "wangwu", 23),
                Tuple3.of(3L, "zhaoliu", 34),
                Tuple3.of(3L, "maqi", 25),
                Tuple3.of(6L, "jiao", 22)

        );
//        inputs.first(2).print();
//        inputs.groupBy(0).sortGroup(2, Order.ASCENDING).first(2).print();
//        inputs.groupBy(0).aggregate(Aggregations.SUM,2).print();

        inputs.groupBy(0).sum(2).print();
    }

    @Test
    public void testDistinct() throws Exception {
        DataSet<Tuple3<Long, String, Integer>> inputs = env.fromElements(
                Tuple3.of(1L, "zhangsan", 28),
                Tuple3.of(3L, "lisi", 34),
                Tuple3.of(3L, "wangwu", 23),
                Tuple3.of(3L, "zhaoliu", 34),
                Tuple3.of(3L, "maqi", 25),
                Tuple3.of(4L, "maqi", 25)
        );
        inputs.distinct(1).print();
    }

    @Test
    public void testPartition() throws Exception {
        ArrayList<Tuple2<Integer, String>> data = new ArrayList<>();
        data.add(new Tuple2<>(1, "hello1"));
        data.add(new Tuple2<>(2, "hello2"));
        data.add(new Tuple2<>(2, "hello3"));
        data.add(new Tuple2<>(3, "hello4"));
        data.add(new Tuple2<>(3, "hello5"));
        data.add(new Tuple2<>(3, "hello6"));
        data.add(new Tuple2<>(4, "hello7"));
        data.add(new Tuple2<>(4, "hello8"));
        data.add(new Tuple2<>(4, "hello9"));
        data.add(new Tuple2<>(4, "hello10"));
        data.add(new Tuple2<>(5, "hello11"));
        data.add(new Tuple2<>(5, "hello12"));
        data.add(new Tuple2<>(5, "hello13"));
        data.add(new Tuple2<>(5, "hello14"));
        data.add(new Tuple2<>(5, "hello15"));
        data.add(new Tuple2<>(6, "hello16"));
        data.add(new Tuple2<>(6, "hello17"));
        data.add(new Tuple2<>(6, "hello18"));
        data.add(new Tuple2<>(6, "hello19"));
        data.add(new Tuple2<>(6, "hello20"));
        data.add(new Tuple2<>(6, "hello21"));
        DataSource<Tuple2<Integer, String>> source = env.fromCollection(data);
        source.partitionByHash(0).mapPartition(new MyRichMapPartitionFunction()).print();
    }

    @Test
    public void testProject() throws Exception {
        DataSource<Tuple3<Long, String, Integer>> inputs = env.fromElements(
                Tuple3.of(1L, "zhangsan", 28),
                Tuple3.of(3L, "wangwu", 34),
                Tuple3.of(3L, "wangwu", 23),
                Tuple3.of(3L, "zhaoliu", 34),
                Tuple3.of(3L, "maqi", 25));
        inputs.project(0, 1).distinct().print();
    }

    @Test
    public void testMapPartition() throws Exception {
        DataSource<Long> longDataSource = env.generateSequence(1, 20);
        longDataSource.mapPartition(new MyMapPartition()).print();
    }

    @Test
    public void testSortPartition() throws Exception {
        ArrayList<Tuple2<Integer, String>> data = new ArrayList<>();
        data.add(new Tuple2<>(2, "zs"));
        data.add(new Tuple2<>(4, "ls"));
        data.add(new Tuple2<>(3, "ww"));
        data.add(new Tuple2<>(1, "xw"));
        data.add(new Tuple2<>(1, "aw"));
        data.add(new Tuple2<>(1, "mw"));
        DataSource<Tuple2<Integer, String>> source = env.fromCollection(data).setParallelism(2);
//        source.first(3).print();
        source.sortPartition(0, Order.ASCENDING).sortPartition(1, Order.DESCENDING).mapPartition(new PartitionMapper()).print();
    }

    @Test
    public void testdefaultJoin() throws Exception {
        //tuple2<用户id，用户姓名>
        ArrayList<Tuple2<Integer, String>> data1 = new ArrayList<>();
        data1.add(new Tuple2<>(1, "zs"));
        data1.add(new Tuple2<>(2, "ls"));
        data1.add(new Tuple2<>(3, "ww"));
        data1.add(new Tuple2<>(4, "zl"));

        //tuple2<用户id，用户所在城市>
        ArrayList<Tuple2<Integer, String>> data2 = new ArrayList<>();
        data2.add(new Tuple2<>(1, "beijing"));
        data2.add(new Tuple2<>(2, "shanghai"));
        data2.add(new Tuple2<>(3, "guangzhou"));
        DataSource<Tuple2<Integer, String>> source = env.fromCollection(data1);
        DataSource<Tuple2<Integer, String>> source1 = env.fromCollection(data2);
//        source.join(source1).where(0).equalTo(0).print();
        source.leftOuterJoin(source1).where(0).equalTo(0).with(new MyJoinFunction()).print();
    }

    @Test
    public void testJoinWithProjection() throws Exception {
        ArrayList<Tuple2<Integer, String>> data1 = new ArrayList<>();
        data1.add(new Tuple2<>(1, "zs"));
        data1.add(new Tuple2<>(2, "ls"));
        data1.add(new Tuple2<>(3, "ww"));
        data1.add(new Tuple2<>(4, "zl"));

        //tuple2<用户id，用户所在城市>
        ArrayList<Tuple2<Integer, String>> data2 = new ArrayList<>();
        data2.add(new Tuple2<>(1, "beijing"));
        data2.add(new Tuple2<>(2, "shanghai"));
        data2.add(new Tuple2<>(3, "guangzhou"));
        DataSource<Tuple2<Integer, String>> input1 = env.fromCollection(data1);
        DataSource<Tuple2<Integer, String>> input2 = env.fromCollection(data2);

        JoinOperator.ProjectJoin projectJoin = input1.join(input2)
                .where(0)
                .equalTo(0).projectFirst(0, 1).projectSecond(1);
        projectJoin.print();
    }


    public static class PartitionMapper extends RichMapPartitionFunction<Tuple2<Integer, String>, Tuple2<Integer, String>> {

        @Override
        public void mapPartition(Iterable<Tuple2<Integer, String>> values, Collector<Tuple2<Integer, String>> out) throws Exception {
            for (Tuple2<Integer, String> item : values) {
                System.out.println("当前subtaskIndex：" + getRuntimeContext().getIndexOfThisSubtask() + "," + item);

            }
        }
    }

    private class MyRichMapPartitionFunction extends RichMapPartitionFunction<Tuple2<Integer, String>, Tuple2<Integer, String>> {
        @Override
        public void mapPartition(Iterable<Tuple2<Integer, String>> values, Collector<Tuple2<Integer, String>> out) throws Exception {
            for (Tuple2<Integer, String> value : values) {
                System.out.println("当前subtaskIndex：" + getRuntimeContext().getIndexOfThisSubtask() + "," + value);
            }
        }
    }

    private class MyMapPartition implements MapPartitionFunction<Long, Tuple2<String, Long>> {
        @Override
        public void mapPartition(Iterable<Long> values, Collector<Tuple2<String, Long>> out) throws Exception {
            String s = "";
            long c = 0;
            for (Long value : values) {
                s += value + ",";
                c++;
            }
            out.collect(Tuple2.of(s, c));
        }
    }

    private class MyJoinFunction implements JoinFunction<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple2<String, String>> {
        @Override
        public Tuple2<String, String> join(Tuple2<Integer, String> first, Tuple2<Integer, String> second) throws Exception {
            return Tuple2.of(first.f1, second == null ? "无" : second.f1);
        }
    }
}
