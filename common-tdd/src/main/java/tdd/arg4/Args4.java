package tdd.arg4;


import tdd.arg4.command.CommandLine;
import tdd.arg4.schema.CommandLineSchema;
import tdd.arg4.schema.SchemaSpc;

public class Args4 {

    private CommandLineSchema schema;

    private CommandLine commandLine;

    public Args4(String commandLine, String schema) {
            this.schema = new CommandLineSchema(schema);
            this.commandLine = new CommandLine(commandLine);
    }

    public Object getValue(String flag) {

        String value = commandLine.getValue(flag);
        SchemaSpc spc = schema.of(flag);
        if (value == null) {
          return null;
        }
        return spc.convertValue(value);
    }
}
