package tdd.mars.invoker.position;

import tdd.mars.area.Position;
import tdd.mars.command.Command;
import tdd.mars.direction.Direction;

public class FPositionInvoker extends AbstractPositionInvoker {


    public FPositionInvoker(Direction direction) {
        super(direction);
    }

    @Override
    protected void whenE(Position position) {

    }

    @Override
    protected void whenN(Position position) {
        int y = position.getY();
        position.setY(--y);
    }

    @Override
    protected void whenS(Position position) {

    }

    @Override
    protected void whenW(Position position) {

    }
}
