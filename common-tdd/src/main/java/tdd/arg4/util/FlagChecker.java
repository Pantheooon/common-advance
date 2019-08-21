package tdd.arg4.util;

import java.util.regex.Pattern;

public class FlagChecker {


    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]{1,}");

    public static boolean isFlag(String str) {
        if (!str.contains("-")) {
            return false;
        }
        return !NUMBER_PATTERN.matcher(removeDash(str)).matches();
    }


    private static String removeDash(String str) {
        assert str != null;
        if (str.contains("-")) {
            return str.substring(1);
        }
        return str;
    }

    public static String getFlag(String str) {
        if (isFlag(str)) {
            return removeDash(str);
        }
        throw new IllegalArgumentException(String.format("the parameter %s is not a flag", str));
    }


    public static boolean isNotFlag(String str) {
        return !isFlag(str);
    }

}
