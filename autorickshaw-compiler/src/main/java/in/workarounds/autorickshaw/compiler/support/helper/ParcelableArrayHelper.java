package in.workarounds.autorickshaw.compiler.support.helper;

import com.squareup.javapoet.ArrayTypeName;

import javax.lang.model.util.Elements;

import in.workarounds.autorickshaw.compiler.model.CargoModel;
import in.workarounds.autorickshaw.compiler.support.SupportResolver;

/**
 * Created by madki on 22/10/15.
 */
public class ParcelableArrayHelper extends TypeHelper {

    public ParcelableArrayHelper(CargoModel cargo, Elements elementUtils) {
        super(cargo);
        if (!(type instanceof ArrayTypeName) || !SupportResolver.isParcelable(((ArrayTypeName) type).componentType, elementUtils)) {
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
}
