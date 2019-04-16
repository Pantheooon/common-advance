package cn.pmj.common.hadoop.mr;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {


    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //把value对应的数据按照分隔符分开
        String[] words = value.toString().split("\t");
        for (String word : words) {
            context.write(new Text(word), new IntWritable(1));
        }
    }
}
