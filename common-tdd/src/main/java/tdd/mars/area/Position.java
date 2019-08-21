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

    @Override
    public void execute(List<Command> commands) {
        for (Command command : commands) {
            PositionInvoker invoker = null;
            switch (command) {
                case r:
                    invoker = new RPositionInvoker(direcrion);
                    continue;
                case l:
                    invoker = new LPositionInvoker(direcrion);
                    continue;
                case f:
                    invoker = new FPositionInvoker(direcrion);
                    continue;
                case b:
                    invoker = new BPositionInvoker(direcrion);
                    continue;
            }
            invoker.invoke(command, this);
        }
    }

    @Override
    public Position get() {
        return this;
    }

    public boolean outOfBoundry() {
        return false;
    }


}
