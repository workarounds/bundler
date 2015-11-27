package in.workarounds.bundler.compiler.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by madki on 20/10/15.
 */
public class StringUtils {

    /**
     * Set of reserved java keywords
     */
    private static final Set<String> javaKeywords = new HashSet<String>(Arrays.asList(
            "abstract",     "assert",        "boolean",      "break",           "byte",
            "case",         "catch",         "char",         "class",           "const",
            "continue",     "default",       "do",           "double",          "else",
            "enum",         "extends",       "false",        "final",           "finally",
            "float",        "for",           "goto",         "if",              "implements",
            "import",       "instanceof",    "int",          "interface",       "long",
            "native",       "new",           "null",         "package",         "private",
            "protected",    "public",        "return",       "short",           "static",
            "strictfp",     "super",         "switch",       "synchronized",    "this",
            "throw",        "throws",        "transient",    "true",            "try",
            "void",         "volatile",      "while"
    ));

    /**
     * Pattern to match part of the class name
     */
    private static final Pattern JAVA_CLASS_NAME_PART_PATTERN =
            Pattern.compile("[A-Za-z_$]+[a-zA-Z0-9_$]*");

    /**
     * Checks if a given name is a valid java class name
     * @param name to be checked
     * @return true if it's a valid java class name
     */
    public static boolean isJavaClassName(String name) {
        if(name == null || name.isEmpty()) return false;
        if(name.endsWith(".") || name.contains("..")) return false;

        for (String part : name.split("\\.")) {
            if (javaKeywords.contains(part) ||
                    !JAVA_CLASS_NAME_PART_PATTERN.matcher(part).matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts a given camel case java variable/class name
     * into a underscore separated all caps name
     * e.g : mainActivity = MAIN_ACTIVITY
     * @param name camel cased variable/class name
     * @return snake cased constant name
     */
    public static String getConstantName(String name) {
        if(name == null) return null;
        if(isConstantName(name)) return name;

        return getSnakeCase(name).toUpperCase();
    }

     /**
     * Converts a given camel case java variable/class name
     * into a underscore separated small caps name
     * e.g : mainActivity = main_activity
     * @param name camel cased variable/class name
     * @return snake cased name
     */
    public static String getSnakeCase(String name) {
        if(name == null) return null;

        if(isSnakeCase(name)) return name;
        if(isConstantName(name)) return name.toLowerCase();

        return name.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
    }

    /**
     * checks if given name is a constant name
     * i.e if the name contains all caps and underscores only
     * @param name to be checked whether constant name
     * @return true if name is constant-name
     */
    public static boolean isConstantName(String name) {
        return Pattern.matches("[\\dA-Z_]+", name);
    }

     /**
     * checks if given name is a snake-cased name
     * i.e if the name contains all small-caps and underscores only
     * @param name to be checked whether snake-cased name
     * @return true if name is snake-cased name
     */
    public static boolean isSnakeCase(String name) {
        return Pattern.matches("[\\da-z_]+", name);
    }

    /**
     * Converts snake cased or constant names to
     * camel cased variable names
     * e.g : main_activity = mainActivity
     * e.g : MAIN_ACTIVITY = MAIN_ACTIVITY
     * @param name snake-case/constant name
     * @return camel cased name
     */
    static String getCamelCase(String name) {
        if(name == null) return null;

        Pattern p = Pattern.compile("_(.)");
        Matcher m = p.matcher(name);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Capitalizes the first character
     * @param name variable name
     * @return name with first character capitalized
     */
    public static String getProperCase(String name) {
        if(name == null) return null;

        String firstChar = Character.toString(name.charAt(0));
        return name.replaceFirst(firstChar, firstChar.toUpperCase());
    }

    /**
     * Converts snake-cased/constant name into
     * java class name
     * e.g : MAIN_ACTIVITY = MainActivity
     * @param name snake-cased/constant name
     * @return name as class name
     */
    public static String getClassName(String name) {
        if(name == null) return null;

        if(isConstantName(name)) name = name.toLowerCase();

        return getProperCase(getCamelCase(name));
    }

     /**
     * Converts snake-cased/constant name into
     * java variable name
     * e.g : MAIN_ACTIVITY = mainActivity
     * @param name snake-cased/constant name
     * @return name as variable name
     */
    public static String getVariableName(String name) {
        if(name == null) return null;

        String className = getClassName(name);
        String firstChar = Character.toString(className.charAt(0));
        return className.replaceFirst(firstChar, firstChar.toLowerCase());
    }

    /**
     * @param value String to be verified if empty
     * @return true if value is either "" or null
     */
    public static boolean isEmpty(String value) {
        return value == null || value.equals("");
    }
}
