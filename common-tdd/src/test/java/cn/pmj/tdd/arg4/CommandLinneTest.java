package cn.pmj.tdd.arg4;

import org.junit.Test;
import tdd.arg4.command.CommandLine;

import static junit.framework.TestCase.assertEquals;

public class CommandLinneTest {


    @Test
    public void test_get_value() {
        String command = "-l -s /usr/local -d 8008";
        CommandLine commandLine = new CommandLine(command);
        assertEquals(commandLine.getValue("l"), "");
        assertEquals(commandLine.getValue("s"), "/usr/local");
        assertEquals(commandLine.getValue("w"), null);

    }


    @Test
    public void test_iregular_value() {
        String command = "-l -p -900 -d 8008";
        CommandLine commandLine = new CommandLine(command);
        assertEquals(commandLine.getValue("l"), "");
        assertEquals(commandLine.getValue("p"), "-900");
        assertEquals(commandLine.getValue("w"), null);
    }
}
