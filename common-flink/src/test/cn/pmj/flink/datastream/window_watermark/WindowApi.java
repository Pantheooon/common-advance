package cn.pmj.flink.datastream.window_watermark;


import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.*;
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

    /**
     * session窗口
     * 比较适合非连续型数据处理或者周期性产生数据的场景
     * 根据用户在线上某段时间内的活跃度对用户行为数据进行统计
     */
    public void testSessionWindow(){
        //基于event time的session
        streamSource.keyBy(new MyKeySelector()).window(EventTimeSessionWindows.withGap(Time.milliseconds(10)));

        //基于process time
        streamSource.keyBy(new MyKeySelector()).window(ProcessingTimeSessionWindows.withGap(Time.milliseconds(10)));

        //动态的调整gap
        streamSource.keyBy(new MyKeySelector()).window(EventTimeSessionWindows.withDynamicGap(new SessionWindowTimeGapExtractor<Log>() {
            @Override
            public long extract(Log element) {
                return 0;
            }
        }));
    }

    /**
     * 滑动窗口
     */
    @Test
    public void testSliding(){
        //窗口1小时,滑动5s
        streamSource.keyBy(new MyKeySelector()).window(SlidingAlignedProcessingTimeWindows.of(Time.hours(1),Time.seconds(5)));

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
