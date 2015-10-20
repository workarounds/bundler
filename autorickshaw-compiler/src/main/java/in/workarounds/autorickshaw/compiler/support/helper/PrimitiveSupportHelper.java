package in.workarounds.autorickshaw.compiler.support.helper;


import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

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
        if(rootType.isArray()) {
            return ParameterSpec.builder(ArrayTypeName.of(support.getType()), label).build();
        } else {
            return ParameterSpec.builder(support.getType(), label).build();
        }
    }

    @Override
    public FieldSpec getBuilderField() {
        if(rootType.isArray()) {
            return FieldSpec.builder(ArrayTypeName.of(support.getType()), label, Modifier.PUBLIC).build();
        } else {
            return FieldSpec.builder(support.getNullableType(), label, Modifier.PUBLIC).build();
        }
    }

    @Override
    public void addToBundle(MethodSpec.Builder bundleBuilder, String BUNDLE_VAR, ClassName KEYS_CLASS) {
        String methodName;
        if(rootType.isArray()) {
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
        if(kind == TypeKind.INT) {
            return PrimitiveSupport.builder()
                    .intentKeyType("INT")
                    .bundlePutMethodName("putInt")
                    .type(int.class)
                    .nullableType(Integer.class)
                    .build();
        } else if(kind == TypeKind.BOOLEAN) {
            return PrimitiveSupport.builder()
                    .intentKeyType("BOOL")
                    .bundlePutMethodName("putBoolean")
                    .type(boolean.class)
                    .nullableType(Boolean.class)
                    .build();
        } else if(kind == TypeKind.CHAR) {
            return PrimitiveSupport.builder()
                    .intentKeyType("CHAR")
                    .bundlePutMethodName("putChar")
                    .type(char.class)
                    .nullableType(Character.class)
                    .build();
        } else if(kind == TypeKind.BYTE) {
            return PrimitiveSupport.builder()
                    .intentKeyType("BYTE")
                    .bundlePutMethodName("putByte")
                    .type(byte.class)
                    .nullableType(Byte.class)
                    .build();
        } else if(kind == TypeKind.DOUBLE) {
            return PrimitiveSupport.builder()
                    .intentKeyType("DOUBLE")
                    .bundlePutMethodName("putDouble")
                    .type(double.class)
                    .nullableType(Double.class)
                    .build();
        } else if(kind == TypeKind.LONG) {
            return PrimitiveSupport.builder()
                    .intentKeyType("LONG")
                    .bundlePutMethodName("putLong")
                    .type(long.class)
                    .nullableType(Long.class)
                    .build();
        } else if(kind == TypeKind.FLOAT) {
            return PrimitiveSupport.builder()
                    .intentKeyType("FLOAT")
                    .bundlePutMethodName("putFloat")
                    .type(float.class)
                    .nullableType(Float.class)
                    .build();
        } else if(kind == TypeKind.SHORT) {
            return PrimitiveSupport.builder()
                    .intentKeyType("SHORT")
                    .bundlePutMethodName("putShort")
                    .type(short.class)
                    .nullableType(Short.class)
                    .build();
        } else {
            throw new UnsupportedClassVersionError("Unrecognized primitive type");
        }
    }

}
