package tdd.arg2;

import java.util.HashMap;
import java.util.Map;

import static tdd.arg2.FlagChecker.*;

public class CommadParser {
    private Map<String, CommandMeta> values = new HashMap();

    public CommadParser(String commad) {
        parseCommand(commad);
    }


    private void parseCommand(String commad) {
        String[] split = commad.split("\\s");
        if (split.length == 1) {
            values.put(getFlag(split[0]), new CommandMeta(split[0], ""));
            return;
        }
        for (int i = 0; i < split.length - 1; ) {
            if (isFlag(split[i])) {
                if (isNotFlag(split[i + 1])) {
                    values.put(getFlag(split[i]), new CommandMeta(split[i], split[i + 1]));
                    i += 2;
                } else {
                    values.put(getFlag(split[i]), new CommandMeta(split[i], ""));
                    i++;
                }

            }
        }
    }


    public String getValue(String flag) {
        return getCommandMeta(flag).getValue();
    }

    public CommandMeta getCommandMeta(String flag){
        return values.get(flag);
    }
}
