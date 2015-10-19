package in.workarounds.autorickshaw.compiler.support.helper;


import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import javax.lang.model.element.Modifier;

import in.workarounds.autorickshaw.compiler.model.PassengerModel;

/**
 * Created by madki on 16/10/15.
 */
public class IntSupportHelper extends SupportHelper {

    public IntSupportHelper(PassengerModel passengerModel) {
        super(passengerModel);
    }

    @Override
    protected ParameterSpec getSetterParameter() {
        if(rootType.isArray()) {
            return ParameterSpec.builder(ArrayTypeName.of(int.class), label).build();
        } else {
            return ParameterSpec.builder(int.class, label).build();
        }
    }

    @Override
    public FieldSpec getBuilderField() {
        if(rootType.isArray()) {
            return FieldSpec.builder(ArrayTypeName.of(int.class), label, Modifier.PUBLIC).build();
        } else {
            return FieldSpec.builder(Integer.class, label, Modifier.PUBLIC).build();
        }
    }

    @Override
    public void addToBundle(MethodSpec.Builder bundleBuilder, String BUNDLE_VAR) {
        if(rootType.isArray()) {
            bundleBuilder.addStatement("$L.putIntArray(\"key\", $L)", BUNDLE_VAR, label);
        } else {
            bundleBuilder.addStatement("$L.putInt(\"key\", $L)", BUNDLE_VAR, label);
        }
    }

    @Override
    public String getTypeForPrefix() {
        return "INT";
    }

    @Override
    public ClassName getFieldType() {
        return ClassName.get(Integer.class);
    }

}
