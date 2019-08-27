package tdd.mars.area;

import lombok.Data;
import tdd.mars.command.Command;
import tdd.mars.direction.Direction;
import tdd.mars.invoker.CommandInvoker;
import tdd.mars.invoker.position.*;

import java.util.List;

@Data
public class Position implements CommandInvoker {

    private Direction direcrion;

    private Mars mars;

    private int x;

    private int y;

    public Position(Direction direcrion, Mars mars, int x, int y) {
        this.direcrion = direcrion;
        this.mars = mars;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute(List<Command> commands) {
        for (Command command : commands) {
            PositionInvoker invoker = null;
            switch (command) {
                case r:
                    invoker = new RPositionInvoker(direcrion);
                    break;
                case l:
                    invoker = new LPositionInvoker(direcrion);
                    break;
                case f:
                    invoker = new FPositionInvoker(direcrion);
                    break;
                case b:
                    invoker = new BPositionInvoker(direcrion);
                    break;
            }
            invoker.invoke(this);
        }
    }

    @Override
    public Position get() {
        return this;
    }

    public boolean outOfBoundry() {
        if (x < 0 || y < 0) {
            return true;
        }

        if (x <= mars.getX() && y <= mars.getY()) {
            return false;
        }

        return true;
    }


}
