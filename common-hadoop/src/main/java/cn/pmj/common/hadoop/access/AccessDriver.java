package cn.pmj.common.hadoop.access;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class AccessDriver {


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration configuration = new Configuration();
        configuration.set("dfs.client.use.datanode.hostname", "true");
        configuration.set("fs.defaultFS", "hdfs://pmj:8020");
        configuration.set("mapreduce.jobtracker.staging.root.dir", "d://tmp");
        Job job = Job.getInstance(configuration);
        job.setJarByClass(AccessDriver.class);
        job.setMapperClass(AccessMapper.class);
        job.setReducerClass(AccessReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Access.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Access.class);

        job.setPartitionerClass(MyPartitioner.class);
        job.setNumReduceTasks(0);
        Path in = new Path("/access/access.log");

        Path out = new Path("/access/access_result");

        FileSystem fileSystem = FileSystem.get(configuration);
        if (fileSystem.exists(out)) {
            fileSystem.delete(out, true);
        }
        FileInputFormat.setInputPaths(job, in);
        FileOutputFormat.setOutputPath(job, out);

        boolean result = job.waitForCompletion(true);
        System.out.println(result);

    }
}
