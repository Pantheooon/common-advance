package cn.pmj.flink.dataset;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.types.StringValue;
import org.junit.Test;

import java.util.Arrays;

public class WordCount {

    ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    @Test
    public void wordCount() throws Exception {
        DataSet<String> text = env.fromElements("Who's there?",
                "I think I hear them. Stand, ho! Who's there?");
        TypeHint<Tuple2<String, Integer>> typeHint = new TypeHint<Tuple2<String, Integer>>() {
        };
        text.flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (value, out) -> {
            for (String s : value.split(" ")) {
                out.collect(new Tuple2<String, Integer>(s, 1));
            }
        }).returns(typeHint).filter((value) -> {
            return !value.f0.contains("?");
        }).groupBy(0).sum(1).print();
    }

    @Test
    public void readLocalText() throws Exception {
        DataSource<String> source = env.readTextFile("file:///D:\\code\\toyrpc");
        source.flatMap((FlatMapFunction<String, String>) (value, out) -> {
            out.collect(value);
        }).returns(Types.STRING).print();
    }


    @Test
    public void readHdfs() throws Exception {
        DataSource<String> source = env.readTextFile("hdfs://192.168.249.134:8200/hello");
        source.flatMap((FlatMapFunction<String, String>) (value, out) -> {
            out.collect(value);
        }).returns(Types.STRING).print();
    }

    //stringvalue
    @Test
    public void readStringValue() throws Exception {
        DataSource<StringValue> source = env.readTextFileWithValue("hdfs://192.168.249.134:8200/hello");
        source.flatMap((FlatMapFunction<StringValue, String>) (value, out) -> {
            out.collect(value.getValue());
        }).returns(Types.STRING).print();
    }

    @Test
    public void readSequence() throws Exception {
        DataSource<String> source = env.readFileOfPrimitives("hdfs://192.168.249.134:8200/hello", String.class);
        source.flatMap((FlatMapFunction<String, String>) (value, out) -> {
            out.collect(value);
        }).returns(Types.STRING).print();
    }

    @Test
    public void fromCollection() {
        DataSource<String> source = env.fromCollection(Arrays.asList("flink", "hadoop"));
        DataSource<String> source1 = env.fromElements("asd", "asdasd");
        //读取文件
//        env.readFile(new PojoCsvInputFormat(),"hdfs://192.168.249.134:8200/hello");
        //从数据库中读取
//       env.createInput();
    }
}
