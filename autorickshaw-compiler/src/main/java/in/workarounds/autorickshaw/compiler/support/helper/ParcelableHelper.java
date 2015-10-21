package in.workarounds.autorickshaw.compiler.support.helper;

import com.squareup.javapoet.ClassName;

import javax.lang.model.util.Elements;

import in.workarounds.autorickshaw.compiler.model.CargoModel;
import in.workarounds.autorickshaw.compiler.support.SupportResolver;
import in.workarounds.autorickshaw.compiler.util.StringUtils;

/**
 * Created by madki on 21/10/15.
 */
public class ParcelableHelper extends TypeHelper {

    public ParcelableHelper(CargoModel cargo, Elements elementUtils) {
        super(cargo);
        if (!(type instanceof ClassName) || !SupportResolver.isParcelable((ClassName) type, elementUtils)) {
            throw new IllegalStateException("ParcelableHelper used for a non parcelable");
        }
    }

    @Override
    public String getIntentKey() {
        return StringUtils.getConstantName(label) + "_" + StringUtils.getConstantName(((ClassName) type).simpleName());
    }

    @Override
    public String getBundleMethodSuffix() {
        return "Parcelable";
    }

    @Override
    public boolean requiresCasting() {
        return true;
    }
}
