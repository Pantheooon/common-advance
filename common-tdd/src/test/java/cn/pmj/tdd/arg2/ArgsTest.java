package cn.pmj.tdd.arg2;

import org.junit.Assert;
import org.junit.Test;
import tdd.arg2.ArgsParser;

public class ArgsTest {

    String commad = "-l -p 8080 -s /user/location";

    String schema = "l:bool,p:int,s:str";

    String commadTwo = "-p -98 -s /s";

    @Test
    public void test_regular() {

        check("l", true, commad);
        check("p", 8080, commad);
        check("s", "/user/location", commad);
    }

    @Test
    public void test_irregular() {
        check("l", false, commadTwo);
        check("p", -98, commadTwo);
        check("s", "/s", commadTwo);
    }

    public void check(String name, Object value, String commands) {
        ArgsParser parser = new ArgsParser(commands, schema);
        Assert.assertEquals(parser.getValueByName(name), value);
    }
}
