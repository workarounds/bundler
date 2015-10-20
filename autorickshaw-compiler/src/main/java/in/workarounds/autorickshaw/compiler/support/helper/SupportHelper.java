package in.workarounds.autorickshaw.compiler.support.helper;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.autorickshaw.compiler.model.PassengerModel;
import in.workarounds.autorickshaw.compiler.model.type.RootType;
import in.workarounds.autorickshaw.compiler.util.StringUtils;

/**
 * Created by madki on 16/10/15.
 */
public abstract class SupportHelper {
    protected final RootType rootType;
    protected final String label;

    public SupportHelper(PassengerModel passengerModel) {
        this.rootType = passengerModel.getType();
        this.label = passengerModel.getLabel();
    }

    public abstract String getTypeForIntentKey();
    protected abstract ClassName getFieldType();

    protected ParameterSpec getSetterParameter() {
        if(rootType.isArray()) {
            return ParameterSpec.builder(ArrayTypeName.of(getFieldType()), label).build();
        } else {
            return ParameterSpec.builder(getFieldType(), label).build();
        }
    }

    public FieldSpec getBuilderField() {
        if(rootType.isArray()) {
            return FieldSpec.builder(getFieldType(), label, Modifier.PRIVATE)
                    .build();
        } else {
            return FieldSpec.builder(ArrayTypeName.of(getFieldType()), label, Modifier.PRIVATE)
                    .build();
        }
    }

    public List<MethodSpec> getBuilderMethods(TypeName builder) {
        List<MethodSpec> methods = new ArrayList<>();

        MethodSpec setter = MethodSpec.methodBuilder(label)
                .addModifiers(Modifier.PUBLIC)
                .returns(builder)
                .addParameter(getSetterParameter())
                .addStatement("this.$L = $L", label, label)
                .addStatement("return this")
                .build();

        methods.add(setter);
        return methods;
    }

    public abstract void addToBundle(MethodSpec.Builder bundleBuilder, String BUNDLE_VAR, ClassName KEYS_CLASS);

    public String getIntentKey() {
        String suffix = getTypeForIntentKey();
        if(rootType.isArray()) {
            suffix =  suffix + "_ARRAY";
        }
        return StringUtils.getConstantName(label) + "_" + suffix;
    }
}
