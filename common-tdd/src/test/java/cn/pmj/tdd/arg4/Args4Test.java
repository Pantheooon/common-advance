package cn.pmj.tdd.arg4;

import org.junit.Assert;
import org.junit.Test;
import tdd.arg4.Args4;

public class Args4Test {


    @Test
    public void test_demo() {
        String commandLine = "-l -p 8080 -d /usr/local";
        String schemaStr = "l:bool,p:int,d:str";
        Args4 args4 = new Args4(commandLine, schemaStr);
        Assert.assertEquals(args4.getValue("l"), true);
        Assert.assertEquals(args4.getValue("p"), 8080);
        Assert.assertEquals(args4.getValue("d"), "/usr/local");

    }


    @Test
    public void test_iregular() {
        String commandLine = "-l false -p -8080 -d";
        String schemaStr = "l:bool,p:int,d:str";
        Args4 args4 = new Args4(commandLine, schemaStr);
        Assert.assertEquals(args4.getValue("l"), false);
        Assert.assertEquals(args4.getValue("p"), -8080);
        Assert.assertEquals(args4.getValue("d"), "");

    }
}
