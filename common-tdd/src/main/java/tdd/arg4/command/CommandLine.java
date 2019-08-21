package tdd.arg4.command;

import java.util.HashMap;
import java.util.Map;

import static tdd.arg4.util.FlagChecker.*;


public class CommandLine {
    private String command ;
    private Map<String,String> map = new HashMap<>();
    public CommandLine(String command) {
        this.command = command;
        parseCommand();
    }

    private void parseCommand(){
        if (command.length() == 0){
            return;
        }
        String[] split = command.split("\\s");
        if (split.length == 1) {
            putEmptyString(split[0]);
            return;
        }
        for (int i = 0; i < split.length ;i++ ) {
            if (isFlag(split[i])) {
                if (i==split.length - 1){
                    putEmptyString(split[i]);
                    break;
                }
                if (isNotFlag(split[i + 1])) {
                    map.put(getFlag(split[i]), split[i+1]);
                } else {
                    putEmptyString(split[i]);
                }

            }
        }

    }


    private void putEmptyString(String flag){
        map.put(getFlag(flag), "");
    }

    public String getValue(String flag) {
        return map.get(flag);
    }
}
