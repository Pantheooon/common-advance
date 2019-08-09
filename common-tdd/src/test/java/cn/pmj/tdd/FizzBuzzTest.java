package cn.pmj.tdd;

import org.junit.Assert;
import org.junit.Test;
import tdd.FizzBuzzGame;

public class FizzBuzzTest {


    @Test
    public void testFizzBuzz() {
        check(1, "1");
        check(2, "2");
        check(3,"Fizz");
        check(5,"Buzz");
        check(15,"FizzBuzz");
        check(13,"Fizz");
        check(52,"Buzz");
    }

    public void check(int raw, String expected) {
        FizzBuzzGame game = new FizzBuzzGame(raw);
        Assert.assertEquals(game.toString(), expected);
    }
}
