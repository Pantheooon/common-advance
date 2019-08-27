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
    public void invoke(Position position) {
        switch (direction) {
            case E:
                whenE(position);
                break;
            case N:
                whenN(position);
                break;
            case S:
                whenS(position);
                break;
            case W:
                whenW(position);
                break;
        }
    }

    protected abstract void whenE(Position position);

    protected abstract void whenN(Position position);

    protected abstract void whenS(Position position);

    protected abstract void whenW(Position position);
}
