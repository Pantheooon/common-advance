package cn.pmj.common.hadoop;

import com.amazonaws.services.dynamodbv2.xspec.S;

import java.util.HashMap;
import java.util.Map;

public class HDFSContext {

    private static Map<String, Integer> context = new HashMap<>();


    public Map<String, Integer> getContext() {
        return context;
    }


    public Integer get(String key) {
        return context.get(key);
    }


    public void put(String key, Integer value) {
        context.put(key, value);
    }

}
