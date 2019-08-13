package tdd.arg2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class SchemaMeta<T> {

    private String name;
    private Object defaultValue;
    private Object nullValue;



    public Object getValue(String value) {
        switch (name) {
            case "bool":
                return Boolean.valueOf(value);
            case "int":
                return Integer.valueOf(value);
            case "str":
                return value;
        }
        return null;
    }
}
