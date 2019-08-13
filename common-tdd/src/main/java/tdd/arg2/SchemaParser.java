package tdd.arg2;

import java.util.HashMap;
import java.util.Map;

public class SchemaParser {


    private SchemaMetaMap  metaMap = new SchemaMetaMap();

    private Map<String,SchemaMeta> map = new HashMap<>();

    public SchemaParser(String schema) {
       parseSchema(schema);
    }



    private void parseSchema(String schema){
        String[] schemas = schema.split(",");
        for (String str : schemas) {
            String[] split = str.split(":");
            map.put(split[0],metaMap.get(split[1]));
        }
    }


    public Object getDefaultValue(String name) {
        return getSchemaMeta(name).getDefaultValue();
    }


    public SchemaMeta getSchemaMeta(String name){
        return map.get(name);
    }
}
