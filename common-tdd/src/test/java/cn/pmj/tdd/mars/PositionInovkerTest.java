package cn.pmj.tdd.mars;

import org.junit.Test;
import tdd.mars.area.Mars;
import tdd.mars.area.Position;
import tdd.mars.command.Command;
import tdd.mars.direction.Direction;

import java.util.ArrayList;
import java.util.List;

public class PositionInovkerTest {


    @Test
    public void test_f_one_when_N() {
        List<Command> commands = new ArrayList<>();
        commands.add(Command.f);
        Position position = new Position(Direction.N, new Mars(10, 8), 10, 9);
        position.execute(commands);
        assert position.getY() == 8;
    }
}
