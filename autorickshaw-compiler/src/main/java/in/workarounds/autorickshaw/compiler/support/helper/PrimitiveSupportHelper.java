package in.workarounds.autorickshaw.compiler.support.helper;


import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

import in.workarounds.autorickshaw.compiler.model.PassengerModel;
import in.workarounds.autorickshaw.compiler.model.type.BasicType;

/**
 * Created by madki on 16/10/15.
 */
public class PrimitiveSupportHelper extends SupportHelper {
    private PrimitiveSupport support;

    public PrimitiveSupportHelper(PassengerModel passengerModel) {
        super(passengerModel);
        support = initPrimitiveSupport();
    }

    @Override
    protected ParameterSpec getSetterParameter() {
        if (rootType.isArray()) {
            return ParameterSpec.builder(ArrayTypeName.of(support.getType()), label).build();
        } else {
            return ParameterSpec.builder(support.getType(), label).build();
        }
    }

    @Override
    public FieldSpec getBuilderField() {
        if (rootType.isArray()) {
            return FieldSpec.builder(ArrayTypeName.of(support.getType()), label, Modifier.PRIVATE).build();
        } else {
            return FieldSpec.builder(support.getNullableType(), label, Modifier.PRIVATE).build();
        }
    }

    @Override
    public List<MethodSpec> getParserMethods(String hasMethodName, String BUNDLE_VAR, ClassName KEYS_CLASS) {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        MethodSpec.Builder getter = MethodSpec.methodBuilder(label)
                .addModifiers(Modifier.PUBLIC);

        if (rootType.isArray()) {
            getter.returns(ArrayTypeName.of(support.getType()))
                    .addStatement("return $L.$L($T.$L)", BUNDLE_VAR, support.getBundleGetArrayMethodName(), KEYS_CLASS, getIntentKey());
        } else {
            String DEFAULT_VALUE = "defaultValue";
            getter.returns(support.getType())
                    .addParameter(support.getType(), DEFAULT_VALUE)
                    .addStatement("return $L.$L($T.$L, $L)", BUNDLE_VAR, support.getBundleGetMethodName(), KEYS_CLASS, getIntentKey(), DEFAULT_VALUE);
        }

        methodSpecs.add(getter.build());
        return methodSpecs;
    }

    @Override
    public void addIntoStatement(MethodSpec.Builder intoBuilder, String DESTINATION_VAR) {
        if(rootType.isArray()) {
            intoBuilder.addStatement("$L.$L = $L()", DESTINATION_VAR, label, label);
        } else {
            intoBuilder.addStatement("$L.$L = $L($L.$L)", DESTINATION_VAR, label, label, DESTINATION_VAR, label);
        }
    }

    @Override
    public void addToBundle(MethodSpec.Builder bundleBuilder, String BUNDLE_VAR, ClassName KEYS_CLASS) {
        String methodName;
        if (rootType.isArray()) {
            methodName = support.getBundlePutArrayMethodName();
        } else {
            methodName = support.getBundlePutMethodName();
        }
        bundleBuilder.addStatement("$L.$L($T.$L, $L)", BUNDLE_VAR, methodName, KEYS_CLASS, getIntentKey(), label);
    }

    @Override
    public String getTypeForIntentKey() {
        return support.getIntentKeyType();
    }

    @Override
    public ClassName getFieldType() {
        return ClassName.get(support.getNullableType());
    }


    private PrimitiveSupport initPrimitiveSupport() {
        TypeKind kind = ((BasicType) rootType).getKind();
        if (kind == TypeKind.INT) {
            return PrimitiveSupport.builder()
                    .intentKeyType("INT")
                    .bundlePutMethodName("putInt")
                    .bundleGetMethodName("getInt")
                    .type(int.class)
                    .nullableType(Integer.class)
                    .build();
        } else if (kind == TypeKind.BOOLEAN) {
            return PrimitiveSupport.builder()
                    .intentKeyType("BOOL")
                    .bundlePutMethodName("putBoolean")
                    .bundleGetMethodName("getBoolean")
                    .type(boolean.class)
                    .nullableType(Boolean.class)
                    .build();
        } else if (kind == TypeKind.CHAR) {
            return PrimitiveSupport.builder()
                    .intentKeyType("CHAR")
                    .bundlePutMethodName("putChar")
                    .bundleGetMethodName("getChar")
                    .type(char.class)
                    .nullableType(Character.class)
                    .build();
        } else if (kind == TypeKind.BYTE) {
            return PrimitiveSupport.builder()
                    .intentKeyType("BYTE")
                    .bundlePutMethodName("putByte")
                    .bundleGetMethodName("getByte")
                    .type(byte.class)
                    .nullableType(Byte.class)
                    .build();
        } else if (kind == TypeKind.DOUBLE) {
            return PrimitiveSupport.builder()
                    .intentKeyType("DOUBLE")
                    .bundlePutMethodName("putDouble")
                    .bundleGetMethodName("getDouble")
                    .type(double.class)
                    .nullableType(Double.class)
                    .build();
        } else if (kind == TypeKind.LONG) {
            return PrimitiveSupport.builder()
                    .intentKeyType("LONG")
                    .bundlePutMethodName("putLong")
                    .bundleGetMethodName("getLong")
                    .type(long.class)
                    .nullableType(Long.class)
                    .build();
        } else if (kind == TypeKind.FLOAT) {
            return PrimitiveSupport.builder()
                    .intentKeyType("FLOAT")
                    .bundlePutMethodName("putFloat")
                    .bundleGetMethodName("getFloat")
                    .type(float.class)
                    .nullableType(Float.class)
                    .build();
        } else if (kind == TypeKind.SHORT) {
            return PrimitiveSupport.builder()
                    .intentKeyType("SHORT")
                    .bundlePutMethodName("putShort")
                    .bundleGetMethodName("getShort")
                    .type(short.class)
                    .nullableType(Short.class)
                    .build();
        } else {
            throw new UnsupportedClassVersionError("Unrecognized primitive type");
        }
    }

}
