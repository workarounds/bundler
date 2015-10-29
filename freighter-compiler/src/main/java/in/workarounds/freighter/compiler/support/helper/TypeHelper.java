package in.workarounds.freighter.compiler.support.helper;

import com.squareup.javapoet.TypeName;

import in.workarounds.freighter.compiler.model.CargoModel;
import in.workarounds.freighter.compiler.util.StringUtils;

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
