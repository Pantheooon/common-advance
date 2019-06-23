package cn.pmj.flink.table;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.InMemoryExternalCatalog;
import org.apache.flink.table.sinks.CsvTableSink;
import org.apache.flink.table.sources.CsvTableSource;
import org.apache.flink.table.sources.TableSource;
import org.junit.Test;

public class TableTest {

    StreamExecutionEnvironment streamEnv = StreamExecutionEnvironment.getExecutionEnvironment();
    StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(streamEnv);

    @Test
    public void intenalCatalog(){
        //注册内部table
        Table table = tableEnv.scan("table").select("id,name,userId");
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
}
