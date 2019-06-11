package cn.pmj.common.flink.helloword.checkpoint;

import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CheckPoint {


    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //每一秒设置个检查点
        env.enableCheckpointing(1000);
        //exactly-once
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //checkpint超时时间
        env.getCheckpointConfig().setCheckpointTimeout(10*60*1000);
        //检查点之间的间隔
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        //最大checkpoint的数量
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        //外部检查点
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //当checkpoint失败时,是否将任务关闭
        env.getCheckpointConfig().setFailOnCheckpointingErrors(false);
        //checkpoint的backend
        env.setStateBackend(new MemoryStateBackend(1000*1024,false));
        env.setStateBackend(new FsStateBackend("file://usr/checkpoint",false));
    }



    static class SavePoint{
        public static void main(String[] args) {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            //设置uid
            env.addSource(null).uid("save-point");
            //savePoint可手工触发
        }
    }
}
