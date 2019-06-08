package cn.pmj.flink.datastream;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.IntegerTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.io.RowCsvInputFormat;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.junit.Test;

import java.util.Arrays;
import java.util.Properties;

public class DataSourceApi {


    static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    @Test
    public void testFileSource() throws Exception {
        //直接读取文本文件
//        DataStreamSource<String> streamSource = env.readTextFile("path");
        //读取csv
        TypeInformation[] typeInformations = new TypeInformation[4];
        typeInformations[0] = IntegerTypeInfo.INT_TYPE_INFO;
        typeInformations[1] = BasicTypeInfo.STRING_TYPE_INFO;
        typeInformations[2]=BasicTypeInfo.STRING_TYPE_INFO;
        typeInformations[3]=BasicTypeInfo.LONG_TYPE_INFO;
        RowCsvInputFormat rowCsvInputFormat = new RowCsvInputFormat(new Path("d:\\test.csv"), typeInformations);
        env.readFile(rowCsvInputFormat,"d:\\test.csv").print();
        env.execute();
        //也可以用PojoCsvInputFormat封装
    }


    @Test
    public void testSocketSource() throws Exception {
        DataStreamSource<String> streamSource = env.socketTextStream("localhost", 8080, ",", 10L);
        streamSource.print();
        env.execute();
    }

    /**
     * 適合本地調試
     */
    @Test
    public void testCollectionSource(){
        env.fromElements(new Tuple2(1,3),new Tuple2(2,4));
        env.fromCollection(Arrays.asList("xxx","xxx"),BasicTypeInfo.STRING_TYPE_INFO);
    }

    /**
     * 外部数据源,例如kafka
     */
    @Test
    public void outerSource(){
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers","localhost:9092");
        //....
        SimpleStringSchema simpleStringSchema = new SimpleStringSchema();
        FlinkKafkaConsumer010<String> topic = new FlinkKafkaConsumer010<>("topic", simpleStringSchema, properties);
        env.addSource(topic);
    }
}
