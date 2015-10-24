package in.workarounds.freighter.compiler.support.helper;

import in.workarounds.freighter.compiler.model.CargoModel;
import in.workarounds.freighter.compiler.util.StringUtils;

/**
 * Created by madki on 21/10/15.
 */
public class PrimitiveHelper extends TypeHelper {

    public PrimitiveHelper(CargoModel cargo) {
        super(cargo);
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
