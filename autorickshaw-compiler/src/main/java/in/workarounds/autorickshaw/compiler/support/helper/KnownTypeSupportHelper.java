package in.workarounds.autorickshaw.compiler.support.helper;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import java.util.List;

import in.workarounds.autorickshaw.compiler.model.PassengerModel;
import in.workarounds.autorickshaw.compiler.model.type.SimpleType;
import in.workarounds.autorickshaw.compiler.util.StringUtils;

/**
 * Created by madki on 21/10/15.
 */
public class KnownTypeSupportHelper extends SupportHelper {
    public KnownTypeSupportHelper(PassengerModel passengerModel) {
        super(passengerModel);
    }

    private SimpleType getSimpleType() {
        return (SimpleType) rootType;
    }

    @Override
    public String getTypeForIntentKey() {
        return StringUtils.getConstantName(getSimpleType().getPrimaryClass().simpleName());
    }

    @Override
    protected ClassName getFieldType() {
        return getSimpleType().getPrimaryClass();
    }

    @Override
    public List<MethodSpec> getParserMethods(String hasMethodName, String BUNDLE_VAR, ClassName KEYS_CLASS) {
        return null;
    }

    @Override
    public void addIntoStatement(MethodSpec.Builder intoBuilder, String DESTINATION_VAR) {

    }

    @Override
    public void addToBundle(MethodSpec.Builder bundleBuilder, String BUNDLE_VAR, ClassName KEYS_CLASS) {

    }
}
