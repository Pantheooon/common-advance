package cn.pmj.common.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataOutputOutputStream;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IOTest {


    public void setUp() {
    }


    /**
     * 测试压缩 支持deflate,gzip,bzip2,lzo,lz4,snappy几种压缩算法
     *
     * @throws IOException
     */
    @Test
    public void codeC() throws IOException {
        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.get(configuration);
        CompressionCodecFactory factory = new CompressionCodecFactory(configuration);
        CompressionCodec codec = factory.getCodec(new Path("D:\\apps\\logs\\AppDebug.gz"));
        String outPut = CompressionCodecFactory.removeSuffix("AppDebug.gz", "gz");
        FSDataInputStream open = fileSystem.open(new Path("D:\\apps\\logs\\AppDebug.gz"));
        CompressionInputStream inputStream = codec.createInputStream(open);
        FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path(outPut));
        IOUtils.copyBytes(inputStream, fsDataOutputStream, configuration);

    }


    @Test
    public void intWriteAble() throws IOException {
        IntWritable intWritable = new IntWritable(163);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputOutputStream = new DataOutputStream(outputStream);
        intWritable.write(dataOutputOutputStream);
        dataOutputOutputStream.close();
        byte[] bytes = outputStream.toByteArray();
        System.out.println(bytes);
    }

}
