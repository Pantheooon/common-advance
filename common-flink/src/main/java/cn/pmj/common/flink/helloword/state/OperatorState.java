package cn.pmj.common.flink.helloword.state;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OperatorState {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        //设置checkpoint
        env.enableCheckpointing(60000L);
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        checkpointConfig.setMinPauseBetweenCheckpoints(30000L);
        checkpointConfig.setCheckpointTimeout(10000L);
        checkpointConfig.setFailOnCheckpointingErrors(true);
        checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);

        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
                3, // number of restart attempts
                Time.of(10, TimeUnit.SECONDS) // delay
        ));
        DataStreamSource<Long> stream = env.fromElements(1L, 2L, 3L, 4L, 5L, 1L, 3L, 4L, 5L, 6L, 7L, 1L, 4L, 5L, 3L, 9L, 9L, 2L, 1L);
        stream.flatMap(new CountWithOperatorState()).setParallelism(3).print();
        env.execute();
    }

    private static class CountWithOperatorState extends RichFlatMapFunction<Long, Tuple2<Integer, String>> implements CheckpointedFunction {


        private transient ListState<Long> checkPointCountList;

        private List<Long> listBufferElements;

        @Override
        public void open(Configuration parameters) throws Exception {
            listBufferElements = new ArrayList<>();
        }

        @Override
        public void flatMap(Long value, Collector<Tuple2<Integer, String>> out) throws Exception {
            if (value == 1) {
                if (listBufferElements.size() > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (Long listBufferElement : listBufferElements) {
                        sb.append(" " + listBufferElement);
                    }
                    out.collect(new Tuple2<>(listBufferElements.size(), sb.toString()));
                    listBufferElements.clear();
                }
            } else {
                listBufferElements.add(value);
            }
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            checkPointCountList.clear();
            for (Long listBufferElement : listBufferElements) {
                checkPointCountList.add(listBufferElement);
            }
        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            ListStateDescriptor<Long> longListStateDescriptor = new ListStateDescriptor<>("checkPointList", TypeInformation.of(new TypeHint<Long>() {
            }));

            checkPointCountList = context.getOperatorStateStore().getListState(longListStateDescriptor);
            if (context.isRestored()) {
                for (Long aLong : checkPointCountList.get()) {
                    listBufferElements.add(aLong);
                }
            }
        }

    }
}
