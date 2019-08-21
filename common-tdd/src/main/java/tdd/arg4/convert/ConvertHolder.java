package tdd.arg4.convert;

import java.util.ArrayList;
import java.util.List;

public class ConvertHolder {


    private static List<Converter> converterList = new ArrayList();


    static {
        converterList.add(new BoolConvert());
        converterList.add(new IntConvert());
        converterList.add(new StringConverter());
    }

    public static Converter getConvert(String type) {
        for (Converter converter : converterList) {
            if (converter.support(type)) {
                return converter;
            }
        }
        return null;
    }
}
