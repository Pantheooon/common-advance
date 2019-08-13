package tdd.arg2;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandMeta {



    private String flag;

    private String value;


    public boolean isDefault(String defaultValue){
        return this.value.equals(defaultValue);
    }
}
