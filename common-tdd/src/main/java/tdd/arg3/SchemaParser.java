package tdd.arg3;

import java.util.HashMap;
import java.util.Map;

public class SchemaParser {

    private String schema;

    private Map<String, SchemaMeta> schemaMap = new HashMap<>();

    public SchemaParser(String schema) {
        this.schema = schema;
        parseSchema();
    }


    public void parseSchema() {
        String[] schemas = schema.split(",");
        for (String str : schemas) {
            String[] split = str.split(":");
            String flag = split[0];
            String type = split[1];
            TypeEnum typeEnum = TypeEnum.getTypeEnum(type);
            schemaMap.put(split[0], new SchemaMeta(typeEnum, flag, type, typeEnum.getDefaultValue()));
        }
    }

    public String getFlagType(String flag) {
        return getSchemaMeta(flag).getType();
    }

    public Object getDefaultValue(String flag) {
        return getSchemaMeta(flag).getDefaultValue();
    }

    public Object getValueByType(String flag, String value) {
        SchemaMeta schemaMeta = getSchemaMeta(flag);
        return schemaMeta.handleValue(value);
    }


    private SchemaMeta getSchemaMeta(String flag) {
        return schemaMap.get(flag);
    }
}
