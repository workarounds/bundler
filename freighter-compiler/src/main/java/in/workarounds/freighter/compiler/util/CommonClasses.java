package in.workarounds.freighter.compiler.util;

import com.squareup.javapoet.ClassName;

/**
 * Created by madki on 19/10/15.
 */
public interface CommonClasses {
    ClassName CONTEXT = ClassName.get("android.content", "Context");
    ClassName BUNDLE = ClassName.get("android.os", "Bundle");
    ClassName INTENT = ClassName.get("android.content", "Intent");
}
