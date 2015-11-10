package in.workarounds.bundler.compiler.support.helper;

import com.squareup.javapoet.TypeName;

/**
 * Created by madki on 21/10/15.
 */
public abstract class TypeHelper {
    protected TypeName type;

    public TypeHelper(TypeName type) {
        this.type = type;
    }

    public abstract String getBundleMethodSuffix();
    public abstract boolean requiresCasting();
}
