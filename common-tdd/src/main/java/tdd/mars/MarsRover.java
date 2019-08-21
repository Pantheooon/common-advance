package tdd.mars;

import tdd.mars.area.Position;
import tdd.mars.command.Command;
import tdd.mars.invoker.CommandInvoker;

import java.util.List;

public class MarsRover implements CommandInvoker {

    private Position position;


    public void execute(List<Command> commands) {
        position.execute(commands);
    }

    @Override
    public Position get() {
        return this.position;
    }


}
