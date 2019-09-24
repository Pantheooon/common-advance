package tdd.mars.invoker;

import tdd.mars.area.Position;
import tdd.mars.command.Command;

import java.util.List;

public interface CommandInvoker {


    void execute(List<Command> commands);

    Position get();

}
