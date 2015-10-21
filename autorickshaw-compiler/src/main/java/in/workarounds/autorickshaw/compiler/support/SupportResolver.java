package in.workarounds.autorickshaw.compiler.support;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.TypeName;

import in.workarounds.autorickshaw.compiler.model.CargoModel;
import in.workarounds.autorickshaw.compiler.support.helper.PrimitiveHelper;
import in.workarounds.autorickshaw.compiler.support.helper.TypeHelper;
import in.workarounds.autorickshaw.compiler.util.Utils;

/**
 * Created by madki on 21/10/15.
 */
public class SupportResolver {

    public static boolean isSupportedType(TypeName typeName) {
        if(typeName.isPrimitive()) {
            return true;
        }

        if(typeName instanceof ArrayTypeName) {
            TypeName componentType = ((ArrayTypeName) typeName).componentType;
            if(componentType.isPrimitive()) {
                return true;
            }
        }

        return false;
    }

    public static TypeHelper getHelper(CargoModel cargo) {
        TypeName type = cargo.getTypeName();
        if(type.isPrimitive()) {
            return new PrimitiveHelper(cargo);
        }
        if(Utils.isPrimitiveArray(type)) {
            return new in.workarounds.autorickshaw.compiler.support.helper.PrimitiveArrayHelper(cargo);
        }

        return null;
    }

}
