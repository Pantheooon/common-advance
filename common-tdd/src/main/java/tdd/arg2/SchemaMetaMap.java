package tdd.arg2;

import java.util.HashMap;
import java.util.Map;

public class SchemaMetaMap {

    private Map<String, SchemaMeta> map = new HashMap();


    public SchemaMetaMap() {
        init();
    }


    public void init() {
        map.put("bool", new SchemaMeta("bool", true, false));
        map.put("int", new SchemaMeta("int", 0, 0));
        map.put("str", new SchemaMeta("str", "", ""));
    }

    public SchemaMeta get(String name) {
        return map.get(name);
    }
}
