package cn.pmj.common.hadoop.project.etl;

import cn.pmj.common.hadoop.project.mr.PageStatApp;
import cn.pmj.common.hadoop.project.util.LogParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Map;

public class ETLApp {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration configuration = new Configuration();
        configuration.set("mapreduce.jobtracker.staging.root.dir","d://tmp");

        Job job = Job.getInstance(configuration);
        job.setJarByClass(ETLApp.class);
        job.setMapperClass(ETLMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);


        Path in = new Path("C:\\Users\\1\\Desktop\\coding301-master\\coding301\\hadoop-train-v2\\input\\raw\\trackinfo_20130721.data");
        Path out = new Path("D:\\pantheon\\code\\common-advance\\common-hadoop\\output\\etl");

        FileInputFormat.setInputPaths(job, in);
        FileOutputFormat.setOutputPath(job, out);

        job.waitForCompletion(true);
    }

    static class ETLMapper extends Mapper<LongWritable, Text, NullWritable,Text>{

        private LogParser logParser = new LogParser();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            Map<String, String> map = logParser.parse(value.toString());
            StringBuffer sb = new StringBuffer();
            sb.append(map.get("ip")+"\t");
            sb.append(map.get("time")+"\t");
            sb.append(map.get("country")+"\t");
            sb.append(map.get("province")+"\t");
            sb.append(map.get("city")+"\t");
            sb.append(map.get("url")+"\t");
            context.write(NullWritable.get(),new Text(sb.toString()));
        }
    }
}
