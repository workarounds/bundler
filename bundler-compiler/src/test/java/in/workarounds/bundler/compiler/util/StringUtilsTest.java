package in.workarounds.bundler.compiler.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by madki on 13/11/15.
 */
public class StringUtilsTest {
    String snakeCase = "main_activity";
    String variableName = "mainActivity";
    String constantName = "MAIN_ACTIVITY";
    String className = "MainActivity";


    @Test
    public void getConstantName() {
        String expected = constantName;

        assertEquals(expected, StringUtils.getConstantName(snakeCase));
        assertEquals(expected, StringUtils.getConstantName(variableName));
        assertEquals(expected, StringUtils.getConstantName(constantName));
        assertEquals(expected, StringUtils.getConstantName(className));
    }

    @Test
    public void getSnakeCase() {
        String expected = snakeCase;

        assertEquals(expected, StringUtils.getSnakeCase(snakeCase));
        assertEquals(expected, StringUtils.getSnakeCase(variableName));
        assertEquals(expected, StringUtils.getSnakeCase(constantName));
        assertEquals(expected, StringUtils.getSnakeCase(className));
    }

    @Test
    public void isConstantName() {
        assertTrue(StringUtils.isConstantName(constantName));
        assertTrue(!StringUtils.isConstantName(variableName));
        assertTrue(!StringUtils.isConstantName(snakeCase));
        assertTrue(!StringUtils.isConstantName(className));
    }

    @Test
    public void isSnakeCase() {
        assertTrue(!StringUtils.isSnakeCase(constantName));
        assertTrue(!StringUtils.isSnakeCase(variableName));
        assertTrue(StringUtils.isSnakeCase(snakeCase));
        assertTrue(!StringUtils.isSnakeCase(className));
    }


    @Test
    public void getCamelCase() {
        assertEquals(variableName, StringUtils.getCamelCase(snakeCase));
        assertEquals(variableName, StringUtils.getCamelCase(variableName));
        assertEquals("MAINACTIVITY", StringUtils.getCamelCase(constantName));
        assertEquals(className, StringUtils.getCamelCase(className));
    }

    @Test
    public void getProperCase() {
        assertEquals(className, StringUtils.getProperCase(className));
        assertEquals(className, StringUtils.getProperCase(variableName));
    }

    @Test
    public void getClassName() {
        String expected = className;

        assertEquals(expected, StringUtils.getClassName(snakeCase));
        assertEquals(expected, StringUtils.getClassName(variableName));
        assertEquals(expected, StringUtils.getClassName(constantName));
        assertEquals(expected, StringUtils.getClassName(className));
    }

    @Test
    public void getVariableName() {
        String expected = variableName;

        assertEquals(expected, StringUtils.getVariableName(snakeCase));
        assertEquals(expected, StringUtils.getVariableName(variableName));
        assertEquals(expected, StringUtils.getVariableName(constantName));
        assertEquals(expected, StringUtils.getVariableName(className));
    }

    @Test
    public void isEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(!StringUtils.isEmpty("S"));
    }

}
