package tdd.mars.invoker.position;

import tdd.mars.area.Position;
import tdd.mars.command.Command;
import tdd.mars.direction.Direction;

public abstract class AbstractPositionInvoker implements PositionInvoker {


    private Direction direction;

    public AbstractPositionInvoker(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void invoke(Command command, Position position) {
        switch (direction) {
            case E:
                whenE(command, position);
                break;
            case N:
                whenN(command, position);
                break;
            case S:
                whenS(command, position);
                break;
            case W:
                whenW(command, position);
                break;
        }
    }

    protected abstract void whenE(Command command, Position position);

    protected abstract void whenN(Command command, Position position);

    protected abstract void whenS(Command command, Position position);

    protected abstract void whenW(Command command, Position position);
}
