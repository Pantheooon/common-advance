package cn.pmj.tdd.arg2;

import org.junit.Assert;
import org.junit.Test;
import tdd.arg2.CommadParser;
import tdd.arg2.FlagChecker;

public class CommadParserTest {



    String commad = "-l -p 8080 -s /user/location";


    @Test
    public void test_regular_commad() {

        check( "-l","l","");
        check("-p 8080","p","8080");
        check("-s /user/location","s","/user/location");
        check("-l -p 8080 -s /user/location","l","");
        check("-l -p 8080 -s /user/location","p","8080");
        check("-l -p 8080 -s /user/location","s","/user/location");

    }


    @Test
    public void test_unregular_commad(){
        String command = "-l false -p -9 -s \"\"";
        check( command,"l","false");
        check( command,"p","-9");
        check( command,"s","\"\"");
    }

    private void check(String commad,String name,String value) {
        CommadParser argsParser = new CommadParser(commad);
        Assert.assertEquals( value,argsParser.getValue(name));
    }



    @Test
    public void testFlagChecker(){
        Assert.assertEquals(true,FlagChecker.isFlag("-p"));
        Assert.assertEquals(false,FlagChecker.isFlag("-9"));
        Assert.assertEquals(false,FlagChecker.isFlag("-0"));
        Assert.assertEquals(true,FlagChecker.isNotFlag("-9"));
        Assert.assertEquals("l",FlagChecker.getFlag("-l"));
    }



    @Test(expected = IllegalArgumentException.class )
    public void test_throw_exception_if_parameter_not_a_flag(){
        FlagChecker.getFlag("-9");
    }
}
