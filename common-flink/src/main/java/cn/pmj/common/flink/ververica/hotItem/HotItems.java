package cn.pmj.common.flink.ververica.hotItem;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.io.PojoCsvInputFormat;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.Collector;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author: Pantheon
 * @date: 2019/5/21 14:16
 * https://zh.ververica.com/developers/computing-real-time-hot-goods/
 */
public class HotItems {

    private final static String CSV_FILE_PATH = "d://UserBehavior.csv";

    private static class TopNHotItems extends KeyedProcessFunction<Tuple, ItemViewCount, String> {

        private final int topSize;

        public TopNHotItems(int topSize) {
            this.topSize = topSize;
        }

        //用于存储商品与点击数的状态,待收齐同一个窗口的数据后,再触发TopN计算
        private ListState<ItemViewCount> itemState;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            //状态的注册
            ListStateDescriptor<ItemViewCount> itemStateDesc = new ListStateDescriptor<>("itemState-state", ItemViewCount.class);
            this.itemState = getRuntimeContext().getListState(itemStateDesc);
        }

        @Override
        public void processElement(ItemViewCount input, Context context, Collector<String> collector) throws Exception {
            //每条数据都保存到状态中
            itemState.add(input);
            //注册windowEnd+1的eventTIme timer,当触发时,说明收齐了属于windowEnd窗口的所有商品数据
            context.timerService().registerEventTimeTimer(input.windowEnd + 1);

        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
            //获取收到的所有商品点击量
            List<ItemViewCount> allItems = new ArrayList();
            for (ItemViewCount itemViewCount : itemState.get()) {
                allItems.add(itemViewCount);
            }
            //提前清除数据中的状态,释放空间
            itemState.clear();
            //按照点击量从大到小排序
            allItems.sort(new Comparator<ItemViewCount>() {
                @Override
                public int compare(ItemViewCount o1, ItemViewCount o2) {
                    return (int) (o2.viewCount - o1.viewCount);
                }
            });
            //将排名信息格式化成string,便于打印
            StringBuffer result = new StringBuffer();
            result.append("===============================\n");
            result.append("时间:").append(DateFormatUtils.format(timestamp - 1, "yyyy-MM-dd HH:mm:ss")).append("\n");
            for (int i = 0; i < topSize; i++) {
                ItemViewCount currentItem = allItems.get(i);
                result.append("No").append(i).append(":")
                        .append("商品ID=").append(currentItem.itemId)
                        .append("浏览量=").append(currentItem.viewCount)
                        .append("\n");
            }
            result.append("=============\n\n");
            out.collect(result.toString());
        }

    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setParallelism(1);
        Path filePath = Path.fromLocalFile(new File(CSV_FILE_PATH));
        PojoTypeInfo<UserBehavior> typeInfo = (PojoTypeInfo) TypeExtractor.createTypeInfo(UserBehavior.class);
        String[] fieldOrder = new String[]{"userId", "itemId", "categoryId", "behavior", "timestamp"};
        PojoCsvInputFormat csvInputFormat = new PojoCsvInputFormat<>(filePath, typeInfo, fieldOrder);
        //输入源
        DataStreamSource dataSource = environment.createInput(csvInputFormat, typeInfo);
        // ProcessingTime：事件被处理的时间。也就是由机器的系统时间来决定。
        // EventTime：事件发生的时间。一般就是数据本身携带的时间。
        environment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        //Watermark 是用来追踪业务事件的概念，可以理解成 EventTime 世界中的时钟，用来指示当前处理到什么时刻的数据了。
        //由于我们的数据源的数据已经经过整理，没有乱序，即事件的时间戳是单调递增的，所以可以将每条数据的业务时间就当做 Watermark。
        DataStream timedData = dataSource.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<UserBehavior>() {
            @Override
            public long extractAscendingTimestamp(UserBehavior userBehavior) {
                //根据数据单位秒,将其转换成毫秒
                return userBehavior.timestamp * 1000;
            }
        });
        //过滤出点击事件
        DataStream pvData = timedData.filter(new FilterFunction<UserBehavior>() {
            @Override
            public boolean filter(UserBehavior userBehavior) throws Exception {
                //点击数据
                return userBehavior.behavior.equals("pv");
            }
        });
        //由于要每隔5分钟统计一次最近一小时每个商品的点击量，所以窗口大小是一小时，每隔5分钟滑动一次。
        // 即分别要统计 [09:00, 10:00), [09:05, 10:05), [09:10, 10:10)… 等窗口的商品点击量。是一个常见的滑动窗口需求（Sliding Window）。
        DataStream windowedData = pvData.keyBy("itemId").timeWindow(Time.minutes(60), Time.minutes(5)).aggregate(new CountAgg(), new WindowResultFunction());
        //点击量前三的商品
        DataStream topItems = windowedData.keyBy("windowEnd").process(new TopNHotItems(3));
        topItems.print();
        environment.execute("Hot Items Job");
    }


    public static class CountAgg implements AggregateFunction<UserBehavior, Long, Long> {

        @Override
        public Long createAccumulator() {
            return 0L;
        }

        @Override
        public Long add(UserBehavior value, Long acc) {
            return acc + 1;
        }

        @Override
        public Long getResult(Long accumulator) {
            return accumulator;
        }

        @Override
        public Long merge(Long a, Long b) {
            return a + b;
        }
    }


    public static class WindowResultFunction implements WindowFunction<Long, ItemViewCount, Tuple, TimeWindow> {

        @Override
        public void apply(
                Tuple key,  // 窗口的主键，即 itemId
                TimeWindow window,  // 窗口
                Iterable<Long> aggregateResult, // 聚合函数的结果，即 count 值
                Collector<ItemViewCount> collector  // 输出类型为 ItemViewCount
        ) throws Exception {
            Long itemId = ((Tuple1<Long>) key).f0;
            Long count = aggregateResult.iterator().next();
            collector.collect(ItemViewCount.of(itemId, window.getEnd(), count));
        }
    }

    public static class ItemViewCount {
        public long itemId;     // 商品ID
        public long windowEnd;  // 窗口结束时间戳
        public long viewCount;  // 商品的点击量

        public static ItemViewCount of(long itemId, long windowEnd, long viewCount) {
            ItemViewCount result = new ItemViewCount();
            result.itemId = itemId;
            result.windowEnd = windowEnd;
            result.viewCount = viewCount;
            return result;
        }
    }

    public static class UserBehavior {
        public long userId;
        public long itemId;
        public int categoryId;
        public String behavior;
        public long timestamp;

        public long getUserId() {
            return userId;
        }

        public UserBehavior setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public long getItemId() {
            return itemId;
        }

        public UserBehavior setItemId(long itemId) {
            this.itemId = itemId;
            return this;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public UserBehavior setCategoryId(int categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public String getBehavior() {
            return behavior;
        }

        public UserBehavior setBehavior(String behavior) {
            this.behavior = behavior;
            return this;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public UserBehavior setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }
    }

}
