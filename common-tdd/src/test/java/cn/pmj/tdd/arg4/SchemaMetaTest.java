package cn.pmj.tdd.arg4;

import tdd.arg4.convert.*;
import tdd.arg4.convert.exception.ConvertException;
import org.junit.Test;
import tdd.arg4.schema.SchemaSpc;

import static org.junit.Assert.assertEquals;

public class SchemaMetaTest {


    @Test
    public void test_override_equals() {
        String l = "l:bool";
        SchemaSpc schemaSpc = new SchemaSpc(l);
        SchemaSpc another = new SchemaSpc(l);
        assertEquals(schemaSpc, another);
    }


    @Test
    public void test_spec_type() {
        String l = "l:bool";
        SchemaSpc schemaSpc = new SchemaSpc(l);
        assertEquals(schemaSpc.getFlag(), "l");
        assertEquals(schemaSpc.getType(), "bool");
    }


    @Test
    public void test_str_convert_value() {
        checkConvertValue("s:str", "/usr/local", "/usr/local");
        checkConvertValue("l:bool", "false", Boolean.FALSE);
        checkConvertValue("d:int", "900", 900);
    }

    private void checkConvertValue(String l, String value, Object expected) {
        SchemaSpc schemaSpc = new SchemaSpc(l);
        Object o = schemaSpc.convertValue(value);
        assertEquals(o, expected);
    }


    @Test
    public void test_string_convert() {
        StringConverter converter = new StringConverter();
        assertEquals(converter.getDefaultValue(), "");
        assertEquals(converter.convert(" "), " ");
    }


    @Test
    public void test_bool_convert() {
        BoolConvert convert = new BoolConvert();
        assertEquals(convert.getDefaultValue(), true);
        assertEquals(convert.convert("true"), true);
        assertEquals(convert.convert("false"), false);
    }


    @Test(expected = ConvertException.class)
    public void test_throw_exption_when_str_is_not_bool_type() {
        BoolConvert convert = new BoolConvert();
        convert.convert("foo");
    }


    @Test(expected = ConvertException.class)
    public void test_throw_exception_when_str_is_not_int_type() {
        IntConvert convert = new IntConvert();
        convert.convert("foo");
    }


    @Test
    public void test_int_convert() {
        IntConvert convert = new IntConvert();
        assertEquals(convert.getDefaultValue(), new Integer(0));
        assertEquals(convert.convert("100"), new Integer(100));
    }

    @Test
    public void test_convert_holder() {
        ConvertHolder holder = new ConvertHolder();
        Converter converter = holder.getConvert("bool");
        assertEquals(converter.convert("true"), true);
    }
}
