package in.workarounds.freighter.compiler.support.helper;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.util.Elements;

import in.workarounds.freighter.compiler.model.CargoModel;
import in.workarounds.freighter.compiler.support.SupportResolver;

/**
 * Created by madki on 22/10/15.
 */
public class ParcelableArrayHelper extends TypeHelper {

    public ParcelableArrayHelper(CargoModel cargo, Elements elementUtils) {
        super(cargo);
        if (!isParcelableArray(type, elementUtils)) {
            throw new IllegalStateException("ParcelableArrayHelper used for a non ParcelableArray type");
        }
    }

    @Override
    public String getBundleMethodSuffix() {
        return "ParcelableArray";
    }

    @Override
    public boolean requiresCasting() {
        return true;
    }

    public static boolean isParcelableArray(TypeName typeName, Elements elementUtils) {
        return (typeName instanceof ArrayTypeName)
                &&
                SupportResolver.isParcelable(
                        ((ArrayTypeName) typeName).componentType,
                        elementUtils
                );
    }
}
