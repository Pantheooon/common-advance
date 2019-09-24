package cn.pmj.tdd.arg3;

import org.junit.Assert;
import org.junit.Test;
import tdd.arg3.Arg;
import tdd.arg3.TypeEnum;

public class ArgsTest {


    @Test
    public void test_regular() {
        String schema = "l:bool,p:int,s:str";
        String commad = "-l -p 8080 -s /usr";
        check(schema, commad, "l", true);
        check(schema, commad, "p", 8080);
        check(schema, commad, "s", "/usr");
    }


    @Test
    public void test_irregular() {
        String schema = "l:bool,p:int,s:str";
        String commad = "-l false -p -9 -s /usr";
        check(schema, commad, "l", false);
        check(schema, commad, "p", -9);
        check(schema, commad, "s", "/usr");
    }


    @Test
    public void checkDefault() {
        String schema = "l:bool,p:int,s:str";
        String commad = "";
        check(schema, commad, "l", true);
        check(schema, commad, "p", 0);
        check(schema, commad, "s", "");
    }


    public void check(String schema, String command, String flag, Object value) {
        Arg arg = new Arg(schema, command);
        Object l = arg.get(flag);
        Assert.assertEquals(l, value);
    }
}
