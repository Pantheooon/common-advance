package tdd.mars.invoker.position;

import tdd.mars.area.Position;
import tdd.mars.command.Command;
import tdd.mars.direction.Direction;

public class LPositionInvoker extends AbstractPositionInvoker {
    public LPositionInvoker(Direction direction) {
        super(direction);
    }

    @Override
    protected void whenE(Command command, Position position) {

    }

    @Override
    protected void whenN(Command command, Position position) {

    }

    @Override
    protected void whenS(Command command, Position position) {

    }

    @Override
    protected void whenW(Command command, Position position) {

    }
}
