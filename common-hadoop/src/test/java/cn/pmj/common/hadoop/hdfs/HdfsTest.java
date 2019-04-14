package cn.pmj.common.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URI;

public class HdfsTest {


    FileSystem fileSystem = null;

    Configuration configuration = new Configuration();
    @Before
    public void setUp() throws IOException, InterruptedException {
        configuration.set("dfs.client.use.datanode.hostname","true");
        URI uri = URI.create("hdfs://pmj:8020");
        fileSystem = FileSystem.get(uri, configuration, "root");
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }


    /**
     * 创建HDFS文件夹
     */
    @Test
    public void mkdir() throws Exception {
        fileSystem.mkdirs(new Path("/hdfsapi/test"));
    }

    /**
     * 查看HDFS内容
     */
    @Test
    public void text()throws Exception {
        FSDataInputStream in = fileSystem.open(new Path("/test.txt"));
        IOUtils.copyBytes(in, System.out, 1024);
    }

    /**
     * 创建文件
     */
    @Test
    public void create()throws Exception {
//        FSDataOutputStream out = fileSystem.create(new Path("/hdfsapi/test/a.txt"));
        FSDataOutputStream out = fileSystem.create(new Path("/hdfsapi/test/b.txt"));
        out.writeUTF("hello pk: replication 1");
        out.flush();
        out.close();
    }

    /**
     * 测试文件名更改
     * @throws Exception
     */
    @Test
    public void rename() throws Exception {
        Path oldPath = new Path("/hdfsapi/test/b.txt");
        Path newPath = new Path("/hdfsapi/test/c.txt");
        boolean result = fileSystem.rename(oldPath, newPath);
        System.out.println(result);

    }


    /**
     * 拷贝本地文件到HDFS文件系统
     */
    @Test
    public void copyFromLocalFile() throws Exception {
        Path src = new Path("d://books.json");
        Path dst = new Path("/hdfsapi/test/");
        fileSystem.copyFromLocalFile(src,dst);
    }

    /**
     * 拷贝大文件到HDFS文件系统：带进度
     */
    @Test
    public void copyFromLocalBigFile() throws Exception {

        InputStream in = new BufferedInputStream(new FileInputStream(new File("d://test.mp4")));

        FSDataOutputStream out = fileSystem.create(new Path("/video.mp4"),
                new Progressable() {
                    public void progress() {
                        System.out.print(".");
                    }
                });

        IOUtils.copyBytes(in, out ,4096);

    }

    /**
     * 拷贝HDFS文件到本地：下载
     */
    @Test
    public void copyToLocalFile() throws Exception {
        Path src = new Path("/test.txt");
        Path dst = new Path("d:\\123.txt");
        fileSystem.copyToLocalFile(false,src, dst,true);
    }


    /**
     * 查看目标文件夹下的所有文件
     */
    @Test
    public void listFiles() throws Exception {
        FileStatus[] statuses = fileSystem.listStatus(new Path("/hdfsapi/test"));

        for(FileStatus file : statuses) {
            String isDir = file.isDirectory() ? "文件夹" : "文件";
            String permission = file.getPermission().toString();
            short replication = file.getReplication();
            long length = file.getLen();
            String path = file.getPath().toString();


            System.out.println(isDir + "\t" + permission
                    + "\t" + replication + "\t" + length
                    + "\t" + path
            );
        }

    }


    /**
     * 递归查看目标文件夹下的所有文件
     */
    @Test
    public void listFilesRecursive() throws Exception {

        RemoteIterator<LocatedFileStatus> files = fileSystem.listFiles(new Path("/hdfsapi/test"), true);

        while (files.hasNext()) {
            LocatedFileStatus file = files.next();
            String isDir = file.isDirectory() ? "文件夹" : "文件";
            String permission = file.getPermission().toString();
            short replication = file.getReplication();
            long length = file.getLen();
            String path = file.getPath().toString();


            System.out.println(isDir + "\t" + permission
                    + "\t" + replication + "\t" + length
                    + "\t" + path
            );
        }
    }


    /**
     * 查看文件块信息
     */
    @Test
    public void getFileBlockLocations() throws Exception {

        FileStatus fileStatus = fileSystem.getFileStatus(new Path("/hdfsapi/test/jdk.tgz"));
        BlockLocation[] blocks = fileSystem.getFileBlockLocations(fileStatus,0,fileStatus.getLen());

        for(BlockLocation block : blocks) {

            for(String name: block.getNames()) {
                System.out.println(name +" : " + block.getOffset() + " : " + block.getLength() + " : " + block.getHosts());
            }
        }
    }

    /**
     * 删除文件
     */
    @Test
    public void delete() throws Exception {
        boolean result = fileSystem.delete(new Path("/hdfsapi/test/jdk.tgz"), true);
        System.out.println(result);
    }


    @Test
    public void testReplication() {
        System.out.println(configuration.get("dfs.replication"));
    }
}
