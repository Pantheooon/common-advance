package cn.pmj.common.hadoop.support;

import cn.pmj.common.hadoop.HDFSContext;
import cn.pmj.common.hadoop.HDFSMapper;

public class WordCountMapper implements HDFSMapper {
    @Override
    public void map(String line, HDFSContext context) {
        String[] words = line.split("\t");

        if (words == null || words.length == 0) {
            return;
        }
        for (String word : words) {
            Integer count = context.get(word);
            count = (count == null ? 1 : ++count);
            context.put(word, count);
        }
    }
}
