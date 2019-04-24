package cn.pmj.common.hadoop.hdfs;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.Test;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class ApiTest {


    @Test
    public void urlTest() throws IOException {
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
        InputStream inputStream = new URL("hdfs://192.168.249.129:8200/anaconda-ks.cfg").openStream();
        String s = IOUtils.toString(inputStream);
        System.out.println(s);
    }

    @Test
    public void fsDataInputStreamTest() throws IOException {
        FileSystem fileSystem = FileSystem.get(new Configuration());
        FSDataInputStream open = fileSystem.open(new Path("C:\\Users\\1\\Desktop\\test.txt"));
        String first = IOUtils.toString(open);
        System.out.println(first);
        System.err.println("-------------------------------\n");
        open.seek(0);
        open.skip(1L);
        String second = IOUtils.toString(open);
        System.out.println(second);
    }

    @Test
    public void progressTest() throws IOException {
        FileSystem fileSystem = FileSystem.get(new Configuration());
        FSDataInputStream open = fileSystem.open(new Path("C:\\Users\\1\\Desktop\\test.txt"));
        String first = IOUtils.toString(open);
        System.out.println(first);
        System.err.println("-------------------------------\n");
        open.seek(0);
        open.skip(1L);
        String second = IOUtils.toString(open);
        System.out.println(second);
    }
}
