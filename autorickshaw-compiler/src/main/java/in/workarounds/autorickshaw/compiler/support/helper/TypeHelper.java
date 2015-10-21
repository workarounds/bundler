package in.workarounds.autorickshaw.compiler.support.helper;

import com.squareup.javapoet.TypeName;

import in.workarounds.autorickshaw.compiler.model.CargoModel;

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

    public abstract String getIntentKey();
    public abstract String getBundleMethodSuffix();
    public abstract boolean requiresCasting();
}
