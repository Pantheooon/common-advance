package tdd.arg4.convert;

public interface Converter<T> {


    T convert(String str);


    T getDefaultValue();

    boolean support(String type);


    String getType();

}
