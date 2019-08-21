package tdd.arg4.convert;

import tdd.arg4.convert.exception.ConvertException;

public class IntConvert implements Converter<Integer> {

    private final String type = "int";

    @Override
    public Integer convert(String str) {
        Integer value;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new ConvertException(str + "is not int type,pls check");
        }
    }

    @Override
    public Integer getDefaultValue() {
        return new Integer(0);
    }

    @Override
    public boolean support(String type) {
        return getType().equals(type);
    }

    @Override
    public String getType() {
        return this.type;
    }
}
