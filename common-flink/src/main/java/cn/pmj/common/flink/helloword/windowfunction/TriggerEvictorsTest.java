package cn.pmj.common.flink.helloword.windowfunction;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamContextEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.evictors.Evictor;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TriggerEvictorsTest {

    private static List<Tuple3<String, String, Long>> generateSource() {
        List<Tuple3<String, String, Long>> list = new ArrayList<>();
        list.add(Tuple3.of("class1", "张三", 100L));
        list.add(Tuple3.of("class1", "李四", 78L));
        list.add(Tuple3.of("class1", "王五", 99L));
        list.add(Tuple3.of("class2", "赵六", 81L));
        list.add(Tuple3.of("class2", "钱七", 59L));
        list.add(Tuple3.of("class2", "马二", 97L));
        return list;
    }

    private static LocalStreamEnvironment env = StreamContextEnvironment.createLocalEnvironment();

    //对进入窗口的数据进行剔除
    public static void main(String[] args) throws Exception {
        env.fromCollection(generateSource()).keyBy(0).
                countWindow(3).evictor(new Evictor() {
            @Override
            public void evictAfter(Iterable elements, int size, Window window, EvictorContext evictorContext) {
                System.out.println(elements);
            }

            @Override
            public void evictBefore(Iterable elements, int size, Window window, EvictorContext evictorContext) {
                Iterator iterator = elements.iterator();
                while (iterator.hasNext()){
                    Object next = iterator.next();
                    iterator.remove();
                }
            }
        }).process(new ProcessWindowFunction<Tuple3<String, String, Long>, Object, Tuple, GlobalWindow>() {
                    @Override
                    public void process(Tuple tuple, Context context, Iterable<Tuple3<String, String, Long>> elements, Collector<Object> out) throws Exception {
                        System.out.println(elements);
                    }
                });
        env.execute();
    }

    //TriggerResult的四个状态的含义.continue:当前不粗发计算,继续等待,Fire:触发计算,PURGE:窗口内部数据清除,但不粗发计算
    //  fire_and_purge:触发计算并清除数据
    //触发器其实是展示的是数据是flink底层怎么根据窗口或者什么去粗发计算的,也可以自己定义,那前面的规则就不会按照默认的去触发
    public static void trigger() throws Exception {
        env.fromCollection(generateSource()).keyBy(0).
                countWindow(3).trigger(CountTrigger.of(3)).
                process(new ProcessWindowFunction<Tuple3<String, String, Long>, Object, Tuple, GlobalWindow>() {
            @Override
            public void process(Tuple tuple, Context context, Iterable<Tuple3<String, String, Long>> elements, Collector<Object> out) throws Exception {
                System.out.println(elements);
            }
        });
        env.execute();
    }

}
