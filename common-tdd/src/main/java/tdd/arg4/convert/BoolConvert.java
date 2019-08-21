package tdd.arg4.convert;

import tdd.arg4.convert.exception.ConvertException;

public class BoolConvert implements Converter<Boolean> {


    private final String type = "bool";

    @Override
    public Boolean convert(String str) {
        if (str.trim().length() == 0){
            str = "true";
        }
        if (!isBooleanStr(str, Boolean.FALSE) && !isBooleanStr(str, Boolean.TRUE)) {
            throw new ConvertException(str + " is not boolean type,pls check");
        }
        return Boolean.valueOf(str);
    }

    private boolean isBooleanStr(String str, Boolean aFalse) {
        return str.toLowerCase().equals(aFalse.toString());
    }

    @Override
    public Boolean getDefaultValue() {
        return Boolean.TRUE;
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
