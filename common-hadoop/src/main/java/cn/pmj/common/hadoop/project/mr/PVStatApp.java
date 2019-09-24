package cn.pmj.common.hadoop.project.mr;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 统计页面访问,一条数据为一次访问
 */
public class PVStatApp {


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
//        System.setProperty("HADOOP_USER_NAME","root");
        org.apache.hadoop.conf.Configuration configuration = new Configuration();
//        configuration.set("dfs.client.use.datanode.hostname","true");
//        configuration.set("fs.defaultFS","hdfs://pmj:8020");
        configuration.set("mapreduce.jobtracker.staging.root.dir", "d://tmp");
        Job job = Job.getInstance(configuration);
        job.setJarByClass(PVStatApp.class);
        job.setMapperClass(PVStatMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(LongWritable.class);


        job.setReducerClass(PVStatReducer.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(LongWritable.class);

        Path in = new Path("C:\\Users\\1\\Desktop\\coding301-master\\coding301\\hadoop-train-v2\\input\\raw\\trackinfo_20130721.data");
        Path out = new Path("D:\\pantheon\\code\\common-advance\\common-hadoop\\output\\v1\\pvstat");

        FileInputFormat.setInputPaths(job, in);
        FileOutputFormat.setOutputPath(job, out);

        job.waitForCompletion(true);
    }


    static class PVStatMapper extends Mapper<LongWritable, Text, NullWritable, LongWritable> {

        private LongWritable ONE = new LongWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            context.write(NullWritable.get(), ONE);
        }
    }

    static class PVStatReducer extends Reducer<NullWritable, LongWritable, NullWritable, LongWritable> {
        @Override
        protected void reduce(NullWritable key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {

            Long count = 0L;
            for (LongWritable value : values) {
                count++;
            }
            context.write(NullWritable.get(), new LongWritable(count));
        }
    }

}




