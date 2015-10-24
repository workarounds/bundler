package in.workarounds.freighter.compiler.support.helper;

import com.squareup.javapoet.TypeName;

import in.workarounds.freighter.compiler.model.CargoModel;
import in.workarounds.freighter.compiler.util.StringUtils;

/**
 * Created by madki on 21/10/15.
 */
public abstract class TypeHelper {
    protected TypeName type;
    protected String label;

    public TypeHelper(CargoModel cargo) {
        this.type = cargo.getTypeName();
        this.label = cargo.getLabel();
    }

    public String getIntentKey() {
        return StringUtils.getConstantName(label);
    }

    public abstract String getBundleMethodSuffix();
    public abstract boolean requiresCasting();
}
