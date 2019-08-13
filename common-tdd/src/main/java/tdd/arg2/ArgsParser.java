package tdd.arg2;

import sun.reflect.misc.ReflectUtil;

public class ArgsParser {

    private CommadParser commadParser;

    private SchemaParser schemaParser;


    public ArgsParser(String commad, String schema) {
        this.commadParser = new CommadParser(commad);
        this.schemaParser = new SchemaParser(schema);

    }

    public Object getValueByName(String name) {
        SchemaMeta schemaMeta = schemaParser.getSchemaMeta(name);
        CommandMeta commandMeta = commadParser.getCommandMeta(name);
        if (commandMeta == null) {
            return schemaMeta.getNullValue();
        }
        if (commandMeta.getValue() == null) {
            return schemaMeta.getDefaultValue();
        }

        return schemaMeta.getValue(commandMeta.getValue());
    }
}
