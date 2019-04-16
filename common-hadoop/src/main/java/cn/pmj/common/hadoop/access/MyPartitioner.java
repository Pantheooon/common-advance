package cn.pmj.common.hadoop.access;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class MyPartitioner extends Partitioner<Text,Access>{
    @Override
    public int getPartition(Text text, Access access, int numPartitions) {
        String phone = text.toString();
        if (phone.startsWith("137")){
            return 0;
        }else if (phone.startsWith("138")){
            return 1;
        }else {
            return 2;
        }
    }
}
