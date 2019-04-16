package cn.pmj.common.hadoop.access;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AccessMapper extends Mapper<LongWritable, Text, Text, Access> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] split = value.toString().split("\t");
        String phone = split[1];
        long up = Long.parseLong(split[split.length - 3]);
        long down = Long.parseLong(split[split.length - 2]);
        context.write(new Text(phone), new Access(phone, up, down));

    }

    public static void main(String[] args) {
        String text = "1363157985066 \t13726230503\t00-FD-07-A4-72-B8:CMCC\t120.196.100.82\ti02.c.aliimg.com\t\t24\t27\t2481\t24681\t200";
        String[] split = text.split("\t");
        System.out.println(split[split.length - 3]);
        System.out.println(split[split.length - 2]);
    }
}
