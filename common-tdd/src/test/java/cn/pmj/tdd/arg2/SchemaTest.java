package cn.pmj.tdd.arg2;

import org.junit.Test;
import tdd.arg2.SchemaMetaMap;
import tdd.arg2.SchemaMeta;
import tdd.arg2.SchemaParser;

import static junit.framework.TestCase.assertEquals;

public class SchemaTest {

    String schema = "l:bool,p:int,s:str";


    @Test
    public void test_schema() {
        check("l:bool","bool","false",Boolean.FALSE,"l");
        check("p:int","int","0",0,"p");
        check("s:str","str","","","s");
        check(schema,"bool","false",Boolean.FALSE,"l");
        check(schema,"int","0",0,"p");
        check(schema,"str","","","s");
    }




    @Test
    public void test_schemaMap(){
        SchemaMetaMap schemaMap = new SchemaMetaMap();
        SchemaMeta boolMeta = schemaMap.get("bool");
        assertEquals(boolMeta,new SchemaMeta("bool",false,"false"));
        SchemaMeta intMeta = schemaMap.get("int");
        assertEquals(intMeta,new SchemaMeta("int",0,"0"));
        SchemaMeta strMeta = schemaMap.get("str");
        assertEquals(strMeta,new SchemaMeta("str","",""));
    }


    @Test
    public void test_default_value(){
        SchemaParser parser = new SchemaParser(schema);
        assertEquals(parser.getDefaultValue("s"),"");
        assertEquals(parser.getDefaultValue("l"),false);
        assertEquals(parser.getDefaultValue("p"),0);
    }



    public void check(String schemaStr,String type,Object nullValue,Object defaultObject,String flag ){
        SchemaParser schema = new SchemaParser(schemaStr);
        SchemaMeta schemaMeta = schema.getSchemaMeta(flag);
        SchemaMeta schemaMeta1 = new SchemaMeta(type,defaultObject,nullValue);
        assertEquals(schemaMeta1,schemaMeta);
    }

}
