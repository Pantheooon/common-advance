package tdd.mars.invoker.position;

import tdd.mars.area.Position;
import tdd.mars.command.Command;

public interface PositionInvoker {

    void invoke(Command command, Position position);

}
