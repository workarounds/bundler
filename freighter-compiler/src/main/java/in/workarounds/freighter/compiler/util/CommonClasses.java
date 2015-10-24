package in.workarounds.freighter.compiler.util;

import com.squareup.javapoet.ClassName;

/**
 * Created by madki on 19/10/15.
 */
public class CommonClasses {
    public static ClassName CONTEXT = ClassName.get("android.content", "Context");
    public static ClassName BUNDLE = ClassName.get("android.os", "Bundle");
    public static ClassName INTENT = ClassName.get("android.content", "Intent");
}
