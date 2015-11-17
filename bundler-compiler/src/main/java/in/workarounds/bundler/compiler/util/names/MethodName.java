package in.workarounds.bundler.compiler.util.names;

import in.workarounds.bundler.compiler.model.ArgModel;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.util.StringUtils;

/**
 * Created by madki on 14/11/15.
 */
public class MethodName {
    public static final String build = "build";
    public static final String parse = "parse";
    public static final String into = "into";
    public static final String start = "start";
    public static final String create = "create";
    public static final String inject = "inject";
    public static final String saveState = "saveState";
    public static final String restoreState = "restoreState";
    public static final String isNull = "isNull";
    public static final String bundle = "bundle";
    public static final String intent = "intent";
    public static final String typeWarning = "typeWarning";

    private MethodName() {
    }

    /**
     * @param model the ReqBundler model for which Bundler build method is needed
     * @return the method name to be used in Bundler class for this model
     */
    public static String build(ReqBundlerModel model) {
        return model.getBundlerMethodName().isEmpty() ?
                VarName.from(model) : model.getBundlerMethodName();
    }

    public static String has(ArgModel arg) {
        return "has" + StringUtils.getClassName(arg.getLabel());
    }

}
