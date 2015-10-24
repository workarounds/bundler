package in.workarounds.freighter.compiler.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by madki on 20/10/15.
 */
public class StringUtils {

    public static String getConstantName(String name) {
        if(name == null) return null;

        return getSnakeCase(name).toUpperCase();
    }

    public static String getSnakeCase(String name) {
        if(name == null) return null;

        return name.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
    }

    public static String getCamelCase(String name) {
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

    public static String getProperCase(String name) {
        if(name == null) return null;

        String firstChar = Character.toString(name.charAt(0));
        return name.replaceFirst(firstChar, firstChar.toUpperCase());
    }

    public static String getClassName(String name) {
        if(name == null) return null;

        return getProperCase(getCamelCase(name));
    }

    public static String getVariableName(String name) {
        if(name == null) return null;

        String className = getClassName(name);
        String firstChar = Character.toString(className.charAt(0));
        return className.replaceFirst(firstChar, firstChar.toLowerCase());
    }
}
