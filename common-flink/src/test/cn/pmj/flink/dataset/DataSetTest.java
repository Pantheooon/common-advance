package cn.pmj.flink.dataset;

import org.apache.flink.api.common.cache.DistributedCache;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.functions.FunctionAnnotation;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.junit.Test;
import scala.Tuple2;
import scala.Tuple3;

import java.io.File;
import java.util.List;

public class DataSetTest {

    ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    DataSource<Long> dataSource = env.generateSequence(0, 100);

    @Test
    public void testBroadcast() {
        //注册缓存
        env.registerCachedFile("file://d:/123", "123");
        DataSource<Long> dataSource = env.generateSequence(0, 100);
        dataSource.map((t) -> {
            return t;
        }).withBroadcastSet(dataSource, "dasss").flatMap(new RichFlatMapFunction<Long, Object>() {
            @Override
            public void open(Configuration parameters) throws Exception {
                List<Object> dasss = getRuntimeContext().getBroadcastVariable("dasss");
                DistributedCache distributedCache = getRuntimeContext().getDistributedCache();
                File file = distributedCache.getFile("123");
            }

            @Override
            public void flatMap(Long value, Collector<Object> out) throws Exception {


            }
        });
    }

    @Test
    public void testCache() {


        dataSource.map((t) -> {
            return t;
        }).withBroadcastSet(dataSource, "dasss").flatMap(new RichFlatMapFunction<Long, Object>() {
            @Override
            public void flatMap(Long value, Collector<Object> out) throws Exception {
                DistributedCache distributedCache = getRuntimeContext().getDistributedCache();

            }
        });
    }


//    语义注解

    /**
     * ForwardedFileds代表数据进入后,对指定指定不进行修改,不参与函数的逻辑计算
     */
    @Test
    public void testForwardedFileds() {
        MapOperator<Long, Object> operator = dataSource.map(null).withForwardedFields("f0->f0");
        //多输入,join,cogroup等操作
        operator.withForwardedFields();
    }


    /**
     * 指定不转发字段,需要参与到函数计算当中
     */
    public void testNonForwardedFileds() {
    }


    /**
     *
     */
    public void testReadFields() {
    }


    //注解方式
    @FunctionAnnotation.ForwardedFields("f0->f0;f1->f2")
    class MyMapFunction implements MapFunction<Tuple3, Tuple2> {
        @Override
        public Tuple2 map(Tuple3 value) throws Exception {
            return null;
        }
    }


}
