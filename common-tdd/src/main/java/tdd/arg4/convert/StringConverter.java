package tdd.arg4.convert;

public class StringConverter implements Converter<String> {


    private final String default_value = "";


    private final String type = "str";

    @Override
    public String convert(String str) {
        return str;
    }

    @Override
    public String getDefaultValue() {
        return default_value;
    }

    @Override
    public boolean support(String type) {
        return getType().equals(type);
    }

    @Override
    public String getType() {
        return type;
    }
}
