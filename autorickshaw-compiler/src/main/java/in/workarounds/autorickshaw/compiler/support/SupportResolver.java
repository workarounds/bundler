package in.workarounds.autorickshaw.compiler.support;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.TypeName;

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

}
