package tdd.mars.command;

import java.util.ArrayList;
import java.util.List;

public class CommandGenerate {


    private List<Command> commands = new ArrayList<>();

    public CommandGenerate addCommand(Command command) {
        commands.add(command);
        return this;
    }


    public List<Command> end() {
        return commands;
    }
}
