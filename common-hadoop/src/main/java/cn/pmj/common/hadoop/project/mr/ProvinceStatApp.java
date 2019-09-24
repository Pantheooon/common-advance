package cn.pmj.common.hadoop.project.mr;

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

public class ProvinceStatApp {


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Path in = new Path("C:\\Users\\1\\Desktop\\coding301-master\\coding301\\hadoop-train-v2\\input\\raw\\trackinfo_20130721.data");
        Path out = new Path("D:\\pantheon\\code\\common-advance\\common-hadoop\\output\\v1\\provincestat");


        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);
        job.setJarByClass(ProvinceStatApp.class);
        job.setMapperClass(ProvinceMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(ProviceReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        FileInputFormat.setInputPaths(job, in);
        FileOutputFormat.setOutputPath(job, out);

        job.waitForCompletion(true);


    }


    static class ProvinceMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

        private final LongWritable ONE = new LongWritable(1);

        private LogParser logParser = new LogParser();

        private Text defaultProvice = new Text("-");

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            Map<String, String> parse = logParser.parse(value.toString());
            String provice = parse.get("province");
            if (provice == null) {
                context.write(defaultProvice, ONE);
            } else {

                context.write(new Text(provice), ONE);
            }
        }
    }


    static class ProviceReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

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
