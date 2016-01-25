package in.workarounds.bundler.compiler.helper;

import com.squareup.javapoet.TypeName;

/**
 * Created by madki on 25/01/16.
 */
public class SerializerTypeHelper extends TypeHelper {
    public SerializerTypeHelper(TypeName type) {
        super(type);
    }

    @Override
    public String getBundleMethodSuffix() {
        return null;
    }

    @Override
    public boolean requiresCasting() {
        return false;
    }
}
