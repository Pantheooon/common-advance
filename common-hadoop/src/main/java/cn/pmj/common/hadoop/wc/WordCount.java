package cn.pmj.common.hadoop.wc;

import cn.pmj.common.hadoop.wc.support.WordCountMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;
import java.util.Set;

public class WordCount {

    private static FileSystem fileSystem = null;

    private static Configuration configuration = new Configuration();

    static {
        configuration.set("dfs.client.use.datanode.hostname", "true");
        URI uri = URI.create("hdfs://pmj:8020");
        try {
            fileSystem = FileSystem.get(uri, configuration, "root");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static HDFSContext context = new HDFSContext();

    /**
     * 1.读取hdfs文件
     * 2.业务处理  ==>mapper
     * 3.缓存结果  ==>context
     * 4.输出到hdfs
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        HDFSMapper mapper = new WordCountMapper();
        RemoteIterator<LocatedFileStatus>files= read("/pmj");
        while (files.hasNext()) {
            LocatedFileStatus next = files.next();
            FSDataInputStream open = fileSystem.open(next.getPath());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(open));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                mapper.map(line, context);
            }
            open.close();
            bufferedReader.close();
        }
        write("/pmj/result");
        fileSystem.close();

        System.out.println("end----->");
    }



    public static RemoteIterator<LocatedFileStatus> read(String inputPath) throws IOException {
        Path input = new Path(inputPath);
        return fileSystem.listFiles(input, false);
    }

    public static void write(String outPath) throws IOException {
        FSDataOutputStream out = fileSystem.create(new Path(outPath));
        Set<Map.Entry<String, Integer>> entries = context.getContext().entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            String result = entry.getKey().toString() + "\t" + entry.getValue().toString()+"\n";
            out.write(result.getBytes());
            out.flush();
        }
        out.close();
    }
}
