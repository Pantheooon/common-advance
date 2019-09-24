package cn.pmj.tdd.arg3;

import org.junit.Assert;
import org.junit.Test;
import tdd.arg3.SchemaParser;

public class SchemaParserTest {


    @Test
    public void test_single_schema() {
        check("l:bool", "l", "bool");
        check("p:str", "p", "str");
        check("s:int", "s", "int");
    }


    @Test
    public void test_multi_schema() {
        check("l:bool,p:int,s:str", "l", "bool");
        check("l:bool,p:int,s:str", "p", "int");
        check("l:bool,p:int,s:str", "s", "str");
    }


    @Test
    public void test_default_value() {
        checkDefaultValue("l:bool,p:int,s:str", "l", true);
        checkDefaultValue("l:bool,p:int,s:str", "p", new Integer(0));
        checkDefaultValue("l:bool,p:int,s:str", "s", "");
    }


    public void checkDefaultValue(String schema, String flag, Object defaultValue) {
        SchemaParser parser = new SchemaParser(schema);
        Assert.assertEquals(defaultValue, parser.getDefaultValue(flag));
    }

    public void check(String schema, String flag, String type) {
        SchemaParser parser = new SchemaParser(schema);
        Assert.assertEquals(parser.getFlagType(flag), type);
    }

}
