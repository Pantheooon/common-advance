package tdd.arg4.schema;


import java.util.*;

public class CommandLineSchema {


    private Map<String, SchemaSpc> spcMap = new HashMap<>();

    public CommandLineSchema(String schemaStr) {
        parseSchema(schemaStr);
    }

    private void parseSchema(String schemaStr) {
        List<String> asList = Arrays.asList(schemaStr.split(","));
        asList.stream().forEach((str) -> {
            SchemaSpc schemaSpc = new SchemaSpc(str);
            spcMap.put(schemaSpc.getFlag(), schemaSpc);
        });
    }

    public int size() {
        return spcMap.size();
    }

    public SchemaSpc of(String flag) {
        SchemaSpc schemaSpc = spcMap.get(flag);
        if (schemaSpc == null) {
            throw new NoSuchElementException(flag + " do not found");
        }
        return schemaSpc;
    }
}
