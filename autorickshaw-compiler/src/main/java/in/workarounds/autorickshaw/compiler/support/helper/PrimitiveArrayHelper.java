package in.workarounds.autorickshaw.compiler.support.helper;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.TypeName;

import in.workarounds.autorickshaw.compiler.model.CargoModel;
import in.workarounds.autorickshaw.compiler.util.StringUtils;

/**
 * Created by madki on 21/10/15.
 */
public class PrimitiveArrayHelper extends TypeHelper {
    private TypeName componentType;

    public PrimitiveArrayHelper(CargoModel cargo) {
        super(cargo);
        if(type instanceof ArrayTypeName) {
            componentType = ((ArrayTypeName) type).componentType;
            if(!componentType.isPrimitive()) {
                throw new IllegalStateException("PrimitiveArrayHelper invoked for a non primitive array");
            }
        } else {
            throw new IllegalStateException("PrimitiveArrayHelper invoked for a non array");
        }
    }

   @Override
    public String getBundleMethodSuffix() {
        return StringUtils.getClassName(componentType.toString()) + "Array";
    }

    @Override
    public boolean requiresCasting() {
        return false;
    }
}
