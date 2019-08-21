package cn.pmj.tdd.mars;

import org.junit.Test;
import tdd.mars.MarsRover;
import tdd.mars.area.Mars;
import tdd.mars.area.Position;
import tdd.mars.command.Command;
import tdd.mars.command.CommandGenerate;

import java.util.List;

public class MarsTest {


    @Test
    public void test_demo() {
        List<Command> command = new CommandGenerate()
                .addCommand(Command.f)
                .addCommand(Command.b).end();

        Position position = new Position();
        Mars mars = new Mars();
        MarsRover rover = new MarsRover();
        rover.execute(command);
        Position position1 = rover.get();
    }
}
