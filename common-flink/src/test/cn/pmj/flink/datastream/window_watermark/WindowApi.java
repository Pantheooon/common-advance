package cn.pmj.flink.datastream.window_watermark;


import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.junit.Test;

import java.util.Collection;

public class WindowApi extends WaterMark {

    /**
     * streamSource.keyBy().
     * window().trigger().
     * evictor().allowedLateness().
     * sideOutputLateData().apply().
     * getSideOutput();
     */
    @Test
    public void first() {
        streamSource.keyBy(new MyKeySelector()).window(new MyWindowAssigner());
        streamSource.windowAll(new MyWindowAssigner());
    }


    /**
     * 滚动窗口
     *
     */
    @Test
    public void testTumbling(){
        //event time
        streamSource.keyBy(new MyKeySelector()).window(TumblingEventTimeWindows.of(Time.seconds(5)));
        //process time
        streamSource.keyBy(new MyKeySelector()).window(TumblingProcessingTimeWindows.of(Time.seconds(5)));
    }


    public class MyWindowAssigner extends WindowAssigner {
        @Override
        public Collection assignWindows(Object element, long timestamp, WindowAssignerContext context) {
            return null;
        }

        @Override
        public Trigger getDefaultTrigger(StreamExecutionEnvironment env) {
            return null;
        }

        @Override
        public TypeSerializer getWindowSerializer(ExecutionConfig executionConfig) {
            return null;
        }

        @Override
        public boolean isEventTime() {
            return false;
        }
    }
}
