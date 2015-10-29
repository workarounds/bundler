package in.workarounds.freighter.compiler.support.helper;

import com.squareup.javapoet.TypeName;

import javax.lang.model.util.Elements;

import in.workarounds.freighter.compiler.model.CargoModel;
import in.workarounds.freighter.compiler.support.SupportResolver;

/**
 * Created by madki on 21/10/15.
 */
public class SerializableHelper extends TypeHelper {

    public SerializableHelper(TypeName typeName, Elements elementUtils) {
        super(typeName);
        if (!SupportResolver.isSerializable(typeName, elementUtils)) {
            throw new IllegalStateException("SerializableHelper used for a non serializable object");
        }
    }

   @Override
    public String getBundleMethodSuffix() {
        return "Serializable";
    }

    @Override
    public boolean requiresCasting() {
        return true;
    }
}
