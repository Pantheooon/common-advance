package tdd;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Args {

    private Map<String, ArgsSchemaMeta> schemaMap = new HashMap<>();

    private Map<String, CommandMeta> commandMap = new HashMap<>();

    public Args(String schema, String command) {
        parseSchema(schema);
        parseCommand(command);
    }

    private void parseCommand(String command) {
        if (command == null || command.length() == 0) {
            return;
        }
        String[] splits = command.split("-");
        for (String split : splits) {
            if (split.length() == 0) {
                continue;
            }
            String[] info = split.split(" ");
            CommandMeta commandMeta = new CommandMeta();
            commandMeta.setFlag(info[0]);
            ArgsSchemaMeta schemaMeta = schemaMap.get(info[0]);
            switch (schemaMeta.getSchemaType()) {
                case "str":
                    commandMeta.setValue(info[1]);
                    break;
                case "bool":
                    commandMeta.setValue(resolveBool(info));
                    break;
                case "int":
                    commandMeta.setValue(Integer.valueOf(info[1]));
                    break;
                case "array":
                    commandMeta.setValue(Arrays.asList(info[1].split(",")));
                    break;
            }

            commandMap.put(info[0], commandMeta);
        }
    }


    private boolean resolveBool(String[] info) {
        if (info.length == 1)
            return true;
        return Boolean.valueOf(info[1]);
    }

    private void parseSchema(String schema) {
        if (schema == null) {
            return;
        }
        String[] splits = schema.split(",");
        for (String split : splits) {
            String[] info = split.split(":");
            ArgsSchemaMeta argsSchema = new ArgsSchemaMeta(info[0].trim(), info[1].trim());
            schemaMap.put(info[0].trim(), argsSchema);

        }
    }


    public Object getValue(String flag) {
        return commandMap.get(flag).getValue();
    }


    @Data
    @AllArgsConstructor
    public class ArgsSchemaMeta {
        private String flag;

        private String schemaType;

    }

    @Data
    public class CommandMeta {

        private String flag;

        private Object value;
    }
}
