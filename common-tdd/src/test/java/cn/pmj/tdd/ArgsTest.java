package cn.pmj.tdd;

import org.junit.Assert;
import org.junit.Test;
import tdd.Args;

public class ArgsTest {


    @Test
    public void args_test() {
        String schema = "l:bool,p:int,s:str";
        String command = "-l -p 8080 -s /user/location";
        Args args = new Args(schema, command);
        Boolean l = (Boolean) args.getValue("l");
        Assert.assertEquals(l, true);
        Assert.assertEquals(new Integer(8080),(Integer)args.getValue("p"));
        Assert.assertEquals("/user/location",args.getValue("s"));
    }
}
