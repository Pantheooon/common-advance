package cn.pmj.tdd.mars;

import org.junit.Test;
import tdd.mars.area.Mars;
import tdd.mars.area.Position;
import tdd.mars.direction.Direction;

public class AreaTest {


    @Test
    public void test_mars() {
        Mars mars = new Mars(10, 10);
        assert mars.getX() == 10;
        assert mars.getY() == 10;
    }


    @Test
    public void test_position_init() {
        Mars mars = new Mars(10, 10);
        Position position = new Position(Direction.N, mars, 5, 6);
        assert position.get() == position;
        assert position.getX() == 5;
        assert position.getY() == 6;
        assert position.getMars() == mars;
    }


    @Test
    public void test_out_of_boundary() {
        Position position = new Position(Direction.N, new Mars(10, 8), 10, 9);
        assert position.outOfBoundry();
        Position position1 = new Position(Direction.N, new Mars(10, 8), 10, 8);
        assert !position1.outOfBoundry();
        Position position2 = new Position(Direction.N, new Mars(10, 8), -1, 8);
        assert position2.outOfBoundry();
    }
}
