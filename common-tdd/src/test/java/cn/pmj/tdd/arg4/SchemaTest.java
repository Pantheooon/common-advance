package cn.pmj.tdd.arg4;

import org.junit.Assert;
import org.junit.Test;
import tdd.arg4.schema.CommandLineSchema;
import tdd.arg4.schema.SchemaSpc;

import java.util.NoSuchElementException;

import static junit.framework.TestCase.assertEquals;

public class SchemaTest {


    @Test
    public void test_shema_size() {
        check_size("l:bool,p:int,d:str", 3);
        check_size("l:bool,p:int,d:str,s:str", 4);
    }


    @Test
    public void test_spec() {
        String schemaStr = "l:bool,p:int,d:str";
        CommandLineSchema schema = new CommandLineSchema(schemaStr);
        SchemaSpc schemaSpc = schema.of("l");
        assertEquals(schemaSpc,new SchemaSpc("l:bool"));
        assertEquals(schema.of("p"),new SchemaSpc("p:int"));
    }


    @Test(expected = NoSuchElementException.class)
    public void test_throw_exception_when_flag_not_found() {
        String schemaStr = "l:bool,p:int,d:str";
        CommandLineSchema schema = new CommandLineSchema(schemaStr);
        SchemaSpc w = schema.of("w");
    }

    public void check_size(String schemaStr, int size) {
        CommandLineSchema schema = new CommandLineSchema(schemaStr);
        Assert.assertEquals(size, schema.size());
    }
}
