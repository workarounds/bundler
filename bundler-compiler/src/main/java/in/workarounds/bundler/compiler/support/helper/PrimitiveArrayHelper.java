package in.workarounds.bundler.compiler.support.helper;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.TypeName;

import in.workarounds.bundler.compiler.util.StringUtils;

/**
 * Created by madki on 21/10/15.
 */
public class PrimitiveArrayHelper extends TypeHelper {

    public PrimitiveArrayHelper(TypeName typeName) {
        super(typeName);
        if(!isPrimitiveArray(type)) {
            throw new IllegalStateException("PrimitiveArrayHelper used for a non PrimitiveArray");
        }
    }

   @Override
    public String getBundleMethodSuffix() {
        return StringUtils.getClassName(((ArrayTypeName) type).componentType.toString()) + "Array";
    }

    @Override
    public boolean requiresCasting() {
        return false;
    }

    public static boolean isPrimitiveArray(TypeName typeName) {
        if(typeName instanceof ArrayTypeName) {
            return ((ArrayTypeName) typeName).componentType.isPrimitive();
        }
        return false;
    }
}
