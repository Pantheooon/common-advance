package tdd.arg3;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchemaMeta {


    private TypeEnum typeEnum;

    private String flag;

    private String type;

    private Object defaultValue;

    public Object handleValue(String value) {
        if (value == null || value.length() == 0){
            return typeEnum.getDefaultValue();
        }
        return typeEnum.handleValue(value);
    }
}
