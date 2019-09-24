package tdd.arg3;

import tdd.arg2.CommandMeta;

import java.util.HashMap;
import java.util.Map;

import static tdd.arg2.FlagChecker.*;
import static tdd.arg2.FlagChecker.getFlag;

public class ComandLineParser {


    private String command;

    private Map<String, String> map = new HashMap<>();

    public ComandLineParser(String commad) {
        this.command = commad;
        parseCommand();
    }

    public void parseCommand() {
        if (command.length() == 0) {
            return;
        }
        String[] split = command.split("\\s");
        if (split.length == 1) {
            putEmptyString(split[0]);
            return;
        }
        for (int i = 0; i < split.length; i++) {
            if (isFlag(split[i])) {
                if (i == split.length - 1) {
                    putEmptyString(split[i]);
                    break;
                }
                if (isNotFlag(split[i + 1])) {
                    map.put(getFlag(split[i]), split[i + 1]);
                } else {
                    putEmptyString(split[i]);
                }

            }
        }

    }


    private void putEmptyString(String flag) {
        map.put(getFlag(flag), "");
    }

    public String getFlagValue(String flag) {
        return map.get(flag);
    }
}
