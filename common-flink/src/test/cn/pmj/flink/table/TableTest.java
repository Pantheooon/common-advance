package cn.pmj.flink.table;

import com.sun.org.apache.regexp.internal.RE;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.OverWindow;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.InMemoryExternalCatalog;
import org.apache.flink.table.descriptors.*;
import org.apache.flink.table.sinks.CsvTableSink;
import org.apache.flink.table.sources.CsvTableSource;
import org.apache.flink.table.sources.TableSource;
import org.apache.flink.types.Row;
import org.junit.Test;

public class TableTest {

    StreamExecutionEnvironment streamEnv = StreamExecutionEnvironment.getExecutionEnvironment();
    StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(streamEnv);
    //注册内部table
    Table table = tableEnv.scan("table").select("id,name,userId");
    @Test
    public void intenalCatalog(){

        tableEnv.registerTable("projectTable",table);
        //tableSource
        TableSource source = new CsvTableSource("path",new String[]{},null);
        tableEnv.registerTableSource("csv",source);
        //tableSink
        CsvTableSink tableSink = new CsvTableSink("path","field");
        tableEnv.registerTableSink("sink",tableSink);
    }

    @Test
    public void externalCatalog(){
        InMemoryExternalCatalog catalog = new InMemoryExternalCatalog("external");
        tableEnv.registerExternalCatalog("externalCatalog",catalog);

    }

    //datastream,dataset==>table
    @Test
    public void dataStreamToTable(){
        DataStreamSource<Long> dataStream = streamEnv.generateSequence(1, 100);
        //注册到table上
        tableEnv.registerDataStream("dataStream",dataStream);
        //转换
        Table table = tableEnv.fromDataStream(dataStream);
    }

    @Test
    public void dataSetToTable(){
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment batchEnv = TableEnvironment.getTableEnvironment(env);
        DataSource<Long> dataSource = env.generateSequence(1, 100);
        batchEnv.registerDataSet("datasource",dataSource);
        Table table = batchEnv.fromDataSet(dataSource);
    }

    @Test
    public void tableToDataStream(){
        //只将insert添加到流中
        DataStream<Row> dataStream = tableEnv.toAppendStream(table, Row.class);
        //boolean标志是更新还是retract操作
        DataStream<Tuple2<Boolean, Row>> tuple2DataStream = tableEnv.toRetractStream(table, Row.class);
    }

    @Test
    public void tableToDataSet(){
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tableEnvi = BatchTableEnvironment.getTableEnvironment(env);
        Table table = tableEnvi.fromDataSet(env.generateSequence(0, 100));
        DataSet<Long> objectDataSet = tableEnvi.toDataSet(table,Long.class);
    }

    @Test
    public void testSchema(){
        //字段位置映射
        DataStreamSource<Long> source = streamEnv.generateSequence(1, 100);
        Table table = tableEnv.fromDataStream(source,"id,name,age...");
        //字段名称映射
        //名称映射
    }

    //外部连接器
    @Test
    public void testTableConnector(){
        Schema schema = new Schema();
        schema.field("field1",Types.SQL_TIMESTAMP);
        tableEnv.connect(new FileSystem().path("xxx"))
                .withFormat(new Csv().field("filed1", Types.STRING)
                        .fieldDelimiter(",").lineDelimiter("\n")
                        .ignoreParseErrors()
                )
                .withSchema(schema).inAppendMode().registerTableSource("MyTable");
    }



    public void testTotal(){
        StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(streamEnv);
        tableEnv.connect(getKafkaConnection())
                .withFormat(getFomrat())
                .withSchema(new Schema().field("id",Types.INT)
                .field("name",Types.STRING).field("rowtime",Types.SQL_TIMESTAMP)
                        //定义为eventTime
                        .rowtime(new Rowtime().timestampsFromField("timestamp").watermarksPeriodicBounded(60000))
                ).inAppendMode().registerTableSource("KafkaTable");
        Table kafkaTable = tableEnv.scan("KafkaTable");
    }

    private FormatDescriptor getFomrat() {
        return null;
    }

    private Kafka getKafkaConnection(){
        Kafka kafka = new Kafka();
        kafka.version("0.10")
                .topic("ccc")
                .startFromLatest()
                .property("zookeerper.connect","xxx")
                .property("bootstrap.servers","xxx");
        return kafka;
    }
}
