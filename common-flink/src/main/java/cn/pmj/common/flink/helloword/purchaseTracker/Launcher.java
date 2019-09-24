package cn.pmj.common.flink.helloword.purchaseTracker;

import cn.pmj.common.flink.helloword.purchaseTracker.function.ConnectedBroadcastProcessFuntion;
import cn.pmj.common.flink.helloword.purchaseTracker.model.Config;
import cn.pmj.common.flink.helloword.purchaseTracker.model.EvaluatedResult;
import cn.pmj.common.flink.helloword.purchaseTracker.model.UserEvent;
import cn.pmj.common.flink.helloword.purchaseTracker.schema.ConfigDeserializationSchema;
import cn.pmj.common.flink.helloword.purchaseTracker.schema.EvaluatedResultSerializationSchema;
import cn.pmj.common.flink.helloword.purchaseTracker.schema.UserEventDeserializationSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Launcher {

    public static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
    public static final String GROUP_ID = "group.id";
    public static final String RETRIES = "retries";
    public static final String INPUT_EVENT_TOPIC = "input-event-topic";
    public static final String INPUT_CONFIG_TOPIC = "input-config-topic";
    public static final String OUTPUT_TOPIC = "output-topic";

    public static final MapStateDescriptor<String, Config> configStateDescriptor = new MapStateDescriptor<String, Config>("configBroadcastState", BasicTypeInfo.STRING_TYPE_INFO, TypeInformation.of(new TypeHint<Config>() {
    }));

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final ParameterTool params = ParameterTool.fromArgs(args);
        env.getConfig().setGlobalJobParameters(params);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        env.enableCheckpointing(60 * 1000);
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setMinPauseBetweenCheckpoints(30 * 1000);
        checkpointConfig.setCheckpointTimeout(10 * 1000);
        checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        //backend
        env.setStateBackend(new MemoryStateBackend());
        //restart
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(0, org.apache.flink.api.common.time.Time.minutes(30)));
        Properties consumerProps = new Properties();
        consumerProps.setProperty(BOOTSTRAP_SERVERS, params.get(BOOTSTRAP_SERVERS));
        consumerProps.setProperty(GROUP_ID, params.get(GROUP_ID));


        //事件流
        FlinkKafkaConsumer010<UserEvent> kafkaUserEventSource = new FlinkKafkaConsumer010<UserEvent>(params.get(INPUT_EVENT_TOPIC), new UserEventDeserializationSchema(), consumerProps);
        KeyedStream<UserEvent, String> userEventStringKeyedStream = env.addSource(kafkaUserEventSource).
                assignTimestampsAndWatermarks(new CustomWatermarkExtractor(Time.hours(24))).
                keyBy(new KeySelector<UserEvent, String>() {
                    @Override
                    public String getKey(UserEvent value) throws Exception {
                        return value.getUserId();
                    }
                });

        //配置流
        FlinkKafkaConsumer010<Config> configStream
                = new FlinkKafkaConsumer010<Config>(params.get(INPUT_CONFIG_TOPIC), new ConfigDeserializationSchema(), consumerProps);

        BroadcastStream<Config> configBroadcastStream = env.addSource(configStream).broadcast(configStateDescriptor);
        //链接两个流
        Properties producerProps = new Properties();
        producerProps.setProperty(BOOTSTRAP_SERVERS, params.get(BOOTSTRAP_SERVERS));
        producerProps.setProperty(RETRIES, "3");

        final FlinkKafkaProducer010 kafkaProducer = new FlinkKafkaProducer010<EvaluatedResult>(
                params.get(OUTPUT_TOPIC),
                new EvaluatedResultSerializationSchema(),
                producerProps);
        kafkaProducer.setLogFailuresOnly(false);
        kafkaProducer.setFlushOnCheckpoint(true);


        SingleOutputStreamOperator<EvaluatedResult> process = userEventStringKeyedStream.connect(configBroadcastStream).process(new ConnectedBroadcastProcessFuntion());
        process.addSink(kafkaProducer);
        env.execute("--->");

    }


    private static class CustomWatermarkExtractor extends BoundedOutOfOrdernessTimestampExtractor<UserEvent> {

        public CustomWatermarkExtractor(org.apache.flink.streaming.api.windowing.time.Time maxOutOfOrderness) {
            super(maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(UserEvent element) {
            return element.getEventTime();
        }

    }
}
