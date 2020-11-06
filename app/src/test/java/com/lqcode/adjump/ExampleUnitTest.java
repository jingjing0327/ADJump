package com.lqcode.adjump;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void main() {
        String text = "跳过1132";
        text = text.toString().replace(" ", "");
        String pattern = "^[0-9]跳过.*";
        String pattern003 = "^[0-9]s跳过.*";
        String pattern004 = "^[0-9]秒跳过.*";
        String pattern002 = "^跳过[\\s\\S]{0,5}";
        boolean isMatches = Pattern.matches(pattern, text);
        boolean isMatches002 = Pattern.matches(pattern002, text);
        boolean isMatches003 = Pattern.matches(pattern003, text);
        boolean isMatches004 = Pattern.matches(pattern004, text);
        System.out.println(isMatches);
        System.out.println(isMatches002);
        System.out.println(isMatches003);
        System.out.println(isMatches004);
    }
}