package cn.pmj.tdd.arg3;

import org.junit.Assert;
import org.junit.Test;
import tdd.arg3.ComandLineParser;

public class CommandLineTest {


    @Test
    public void test_single_commandLine() {
        check("-p 8080", "8080", "p");
        check("-s /usr/", "/usr/", "s");
        check("-l", "", "l");
    }


    @Test
    public void test_multi_commandLine() {
        String command = "-l -p 8080 -s /usr";
        check(command, "8080", "p");
        check(command, "/usr", "s");
        check(command, "", "l");
    }


    @Test
    public void test_irregula_commandLine() {
        String command = "-l true -p -9 -s";
        check(command, "-9", "p");
        check(command, "", "s");
        check(command, "true", "l");
    }


    public void check(String commandLine, String expectedValue, String flag) {
        ComandLineParser parser = new ComandLineParser(commandLine);
        Assert.assertEquals(expectedValue, parser.getFlagValue(flag));
    }
}
