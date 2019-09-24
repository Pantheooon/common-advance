package tdd.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Pantheon
 * @date: 2019/9/19 1:47
 * @comment
 */
public class DataBaseInfo {

    private List<String> databases = new ArrayList<>();


    private Map<String, List<String>> tables = new HashMap<>();

    public DataBaseInfo() {
        databases.add("uke1-mysql1");
        databases.add("test-mysql2");
        databases.add("zm-mysql3");
        databases.add("uke2-mysql4");
    }

    public List<String> getDatabases() {
        return databases;
    }
}
