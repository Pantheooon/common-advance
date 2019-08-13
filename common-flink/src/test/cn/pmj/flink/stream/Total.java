package cn.pmj.flink.stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Total {


    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    @Test
    public void test() throws Exception {
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        DataStreamSource<Student> source = env.addSource(new MySource()).setParallelism(2);
        SingleOutputStreamOperator<Student> flatMap = source.flatMap(new FlatMapFunction<Student, Student>() {
            @Override
            public void flatMap(Student value, Collector<Student> out) throws Exception {
                out.collect(value);
            }
        });
        SingleOutputStreamOperator<Student> parallelism = flatMap.setParallelism(1);
        SingleOutputStreamOperator<Student> map = parallelism.map(new MapFunction<Student, Student>() {
            @Override
            public Student map(Student value) throws Exception {
                return value;
            }
        });
        KeyedStream<Student, String> keyedStream = map.keyBy(new KeySelector<Student, String>() {
            @Override
            public String getKey(Student value) throws Exception {
                return value.getName();
            }
        });
        SingleOutputStreamOperator<Student> valueState = keyedStream.process(new KeyedProcessFunction<String, Student, Student>() {

            private MapState<String, Integer> mapState;

            @Override
            public void open(Configuration parameters) throws Exception {
                MapStateDescriptor<String, Integer> descriptor = new MapStateDescriptor("valueState", TypeInformation.of(String.class), TypeInformation.of(Integer.class));
                this.mapState = getRuntimeContext().getMapState(descriptor);
            }

            @Override
            public void processElement(Student value, Context ctx, Collector<Student> out) throws Exception {
                Integer count = mapState.get(ctx.getCurrentKey());
                count = count == null ? 1 : ++count;
                mapState.put(ctx.getCurrentKey(), count);
                out.collect(value);

                ctx.timerService().registerProcessingTimeTimer(System.currentTimeMillis() + 100);
            }

            @Override
            public void onTimer(long timestamp, OnTimerContext ctx, Collector<Student> out) throws Exception {
                String format = "学员:%s,访问次数:%d";
                String currentKey = ctx.getCurrentKey();
                Integer count = mapState.get(currentKey);
                System.err.println(String.format(format, currentKey, count));
            }
        });

        DataStreamSink<Student> studentDataStreamSink = valueState.addSink(new SinkFunction<Student>() {
            @Override
            public void invoke(Student value, Context context) throws Exception {
                System.out.println(value);
            }
        });
        env.execute();
    }


   static class MySource extends RichSourceFunction<Student> implements ParallelSourceFunction<Student> {


        private List<String> names = Arrays.asList("张三","李四","王五","赵六");
        private List<String> subjects = Arrays.asList("英語","语文","数学","地理","化学","政治");

        private ExecutorService executors;

        private volatile boolean flag = true;
        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            executors = Executors.newFixedThreadPool(2);
        }

        @Override
        public void run(SourceContext<Student> ctx) throws Exception {
            while (flag){
                executors.execute(()->{
                    try {
                        TimeUnit.MICROSECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int i = (int)(1+Math.random()*100);
                    String name = this.names.get(i % 4);
                    String subject = this.subjects.get(i % 5);
                    ctx.collect(new Student(name,subject,i,System.currentTimeMillis()));

                });
            }

        }

        @Override
        public void cancel() {
            flag = false;
            executors.shutdown();
        }
    }

    @Data
    @AllArgsConstructor
    @ToString
   static class Student{
        private String name;
        private String subject;
        private Integer score;
        private Long timeStamp;
    }
}
