package in.workarounds.bundler.compiler.helper;

import com.squareup.javapoet.TypeName;

import in.workarounds.bundler.compiler.util.StringUtils;

/**
 * Created by madki on 21/10/15.
 */
public class PrimitiveHelper extends TypeHelper {

    public PrimitiveHelper(TypeName typeName) {
        super(typeName);
        if (!type.isPrimitive()) {
            throw new IllegalStateException("PrimitiveHelper invoked for a non primitive type");
        }
    }

   @Override
    public String getBundleMethodSuffix() {
        return StringUtils.getClassName(type.toString());
    }

    @Override
    public boolean requiresCasting() {
        return false;
    }
}
