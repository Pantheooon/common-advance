package cn.pmj.common.hadoop.hdfs;

import java.io.File;

public class FileTest {

    public static void main(String[] args) {
        File file = new File("d:/1127428771");
        File file2 = new File("d:/1127428771/.staging");
        System.out.println(file.mkdir());
        System.out.println(file2.mkdir());
    }
}
