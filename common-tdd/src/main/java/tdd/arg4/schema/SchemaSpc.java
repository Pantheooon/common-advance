package tdd.arg4.schema;

import lombok.Data;
import tdd.arg4.convert.ConvertHolder;
import tdd.arg4.convert.Converter;

@Data
public class SchemaSpc {

    private String flag;

    private String type;

    private Converter converter;


    public SchemaSpc(String schema) {
        String[] split = schema.split(":");
        this.flag = split[0];
        this.type = split[1];
        converter = ConvertHolder.getConvert(getType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SchemaSpc) {
            SchemaSpc another = (SchemaSpc) obj;
            return another.getFlag().equals(this.flag)
                    && another.getType().equals(this.getType());
        }
        return false;
    }

    public Object convertValue(String value) {
        return converter.convert(value);
    }
}
