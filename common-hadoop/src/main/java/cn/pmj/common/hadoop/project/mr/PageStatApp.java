package cn.pmj.common.hadoop.project.mr;

import cn.pmj.common.hadoop.project.util.GetPageId;
import cn.pmj.common.hadoop.project.util.LogParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Map;

public class PageStatApp {


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration configuration = new Configuration();
        configuration.set("mapreduce.jobtracker.staging.root.dir", "d://tmp");

        Job job = Job.getInstance(configuration);
        job.setJarByClass(PageStatApp.class);
        job.setMapperClass(PageStatMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(PageStatReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        Path in = new Path("C:\\Users\\1\\Desktop\\coding301-master\\coding301\\hadoop-train-v2\\input\\raw\\trackinfo_20130721.data");
        Path out = new Path("D:\\pantheon\\code\\common-advance\\common-hadoop\\output\\v1\\pagestat");

        FileInputFormat.setInputPaths(job, in);
        FileOutputFormat.setOutputPath(job, out);

        job.waitForCompletion(true);
    }


    static class PageStatMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
        private LogParser logParser = new LogParser();

        private LongWritable ONE = new LongWritable(1);

        private Text defaultText = new Text("--");

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            Map<String, String> parse = logParser.parse(value.toString());
            String url = parse.get("url");
            String pageId = GetPageId.getPageId(url);
            if (pageId == null || pageId.length() == 0) {
                context.write(defaultText, ONE);
            } else {
                context.write(new Text(pageId), ONE);
            }
        }
    }


    static class PageStatReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            Long count = 0L;
            for (LongWritable value : values) {
                count++;
            }
            context.write(key, new LongWritable(count));

        }
    }

}
