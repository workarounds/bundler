package in.workarounds.bundler.compiler.util.names;

import com.squareup.javapoet.ClassName;

import in.workarounds.bundler.compiler.model.AnnotatedField;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.util.StringUtils;

/**
 * Created by madki on 14/11/15.
 */
public class VarName {
    public static final String bundle  = from(ClassProvider.bundle);
    public static final String context = from(ClassProvider.context);
    public static final String intent  = from(ClassProvider.intent);

    public static final String defaultVal = "defaultVal";
    public static final String parser     = "parser";

    public static final String tag = "TAG";

    private VarName() {
    }

    public static String from(AnnotatedField field) {
        return field.getLabel();
    }

    public static String from(ReqBundlerModel model) {
        return from(model.getSimpleName());
    }

    public static String from(ClassName className) {
        return StringUtils.getVariableName(className.simpleName());
    }

    public static String from(String name) {
        return StringUtils.getVariableName(name);
    }

}
