package tdd.arg3;

public class Arg {


    private SchemaParser schemaParser;

    private ComandLineParser comandLineParser;

    public Arg(String schema, String commad) {
        schemaParser = new SchemaParser(schema);
        comandLineParser = new ComandLineParser(commad);
    }

    public Object get(String flag){
        return  schemaParser.getValueByType(flag,comandLineParser.getFlagValue(flag));

    }

}
