package in.workarounds.freighter.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.swing.plaf.nimbus.State;

import in.workarounds.freighter.compiler.Provider;
import in.workarounds.freighter.compiler.model.AnnotatedField;
import in.workarounds.freighter.compiler.model.CargoModel;
import in.workarounds.freighter.compiler.model.FreighterModel;
import in.workarounds.freighter.compiler.model.StateModel;
import in.workarounds.freighter.compiler.util.CommonClasses;
import in.workarounds.freighter.compiler.util.StringUtils;

/**
 * Created by madki on 19/10/15.
 */
public class Writer {
    protected Provider provider;
    protected FreighterModel freighterModel;
    protected List<CargoModel> cargoList;
    protected List<StateModel> states;
    protected static final String FILE_PREFIX = "Freighter";
    protected static final String KEYS_SIMPLE_NAME = "Keys";
    protected static final String SUPPLIER_NAME = "Supplier";
    protected static final String RETRIEVER_NAME = "Retriever";
    protected static final String SUPPLY_METHOD = "supply";
    protected static final String RETRIEVE_METHOD = "retrieve";
    protected static final String INTO_METHOD = "into";
    protected static final String BUNDLE_METHOD = "bundle";
    protected static final String INTENT_METHOD = "intent";
    protected static final String START_METHOD = "start";
    protected static final String CREATE_METHOD = "create";
    protected static final String INJECT_METHOD = "inject";
    protected static final String SAVE_METHOD = "saveState";
    protected static final String RESTORE_METHOD = "restoreState";
    protected static final String RETRIEVER_VAR = "retriever";


    protected String DESTINATION_VAR;
    protected String FILE_SIMPLE_NAME;
    protected ClassName SUPPLIER_CLASS;
    protected ClassName RETRIEVER_CLASS;

    protected ClassName KEYS_CLASS;

    protected String CONTEXT_VAR = "context";
    protected String BUNDLE_VAR = "bundle";
    protected String DEFAULT_VAR = "defaultValue";
    protected static final String INTENT_VAR = "intent";

    public static Writer from(Provider provider, FreighterModel freighterModel, List<CargoModel> cargoList, List<StateModel> states) {
        switch (freighterModel.getVariety()) {
            case ACTIVITY:
                return new ActivityWriter(provider, freighterModel, cargoList, states);
            case SERVICE:
                return new ServiceWriter(provider, freighterModel, cargoList, states);
            case FRAGMENT:
            case FRAGMENT_V4:
                return new FragmentWriter(provider, freighterModel, cargoList, states);
            default:
                return new OtherWriter(provider, freighterModel, cargoList, states);
        }
    }


    protected Writer(Provider provider, FreighterModel freighterModel, List<CargoModel> cargoList, List<StateModel> states) {
        this.provider = provider;
        this.freighterModel = freighterModel;
        this.cargoList = cargoList;
        this.states = states;

        DESTINATION_VAR = StringUtils.getVariableName(freighterModel.getSimpleName());
        FILE_SIMPLE_NAME = FILE_PREFIX + freighterModel.getSimpleName();

        String FILE_NAME = freighterModel.getPackageName() + "." + FILE_SIMPLE_NAME;
        SUPPLIER_CLASS = ClassName.bestGuess(FILE_NAME + "." + SUPPLIER_NAME);
        RETRIEVER_CLASS = ClassName.bestGuess(FILE_NAME + "." + RETRIEVER_NAME);
        KEYS_CLASS = ClassName.bestGuess(FILE_NAME + "." + KEYS_SIMPLE_NAME);

    }

    public JavaFile brewJava() {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(FILE_SIMPLE_NAME)
                .addModifiers(Modifier.PUBLIC);
        // save, restore
        classBuilder
                .addMethod(saveMethod())
                .addMethod(restoreMethod());
        // supply, retrieve
        classBuilder
                .addMethod(supplyMethod())
                .addMethod(retrieveBundleMethod())
                .addMethods(getAdditionalHelperMethods())
                .addType(createSupplierClass())
                .addType(createRetrieverClass())
                .addType(createKeysInterface());
        return JavaFile.builder(freighterModel.getPackageName(), classBuilder.build()).build();
    }

    protected MethodSpec saveMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(SAVE_METHOD)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(freighterModel.getClassName(), DESTINATION_VAR)
                .addParameter(CommonClasses.BUNDLE, BUNDLE_VAR)
                .beginControlFlow("if($L == null)", BUNDLE_VAR)
                .addStatement("$L = new $T()", BUNDLE_VAR, CommonClasses.BUNDLE)
                .endControlFlow();

        String label;
        TypeName type;
        for (AnnotatedField state : states) {
            label = state.getLabel();
            type = state.getTypeName();

            if (type.isPrimitive()) {
                builder.addStatement("$L.put$L($S, $L.$L)",
                        BUNDLE_VAR, state.getBundleMethodSuffix(), label, DESTINATION_VAR, label);
            } else {
                builder.beginControlFlow("if($L.$L != null)", DESTINATION_VAR, label)
                        .addStatement("$L.put$L($S, $L.$L)",
                                BUNDLE_VAR, state.getBundleMethodSuffix(), label, DESTINATION_VAR, label)
                        .endControlFlow();
            }
        }
        return builder.build();
    }

    protected MethodSpec restoreMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(RESTORE_METHOD)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(freighterModel.getClassName(), DESTINATION_VAR)
                .addParameter(CommonClasses.BUNDLE, BUNDLE_VAR)
                .beginControlFlow("if($L == null)", BUNDLE_VAR)
                .addStatement("return")
                .endControlFlow();

        String label;
        TypeName type;
        for (AnnotatedField state : states) {
            label = state.getLabel();
            type = state.getTypeName();

            if (type.isPrimitive()) {
                builder.addStatement("$L.$L = $L.get$L($S, $L.$L)",
                        DESTINATION_VAR, label, BUNDLE_VAR, state.getBundleMethodSuffix(), label, DESTINATION_VAR, label);
            } else {
                builder.beginControlFlow("if($L.containsKey($S))", BUNDLE_VAR, label);
                if (state.requiresCasting()) {
                    builder.addStatement("$L.$L = ($T) $L.get$L($S)",
                            DESTINATION_VAR, label, type, BUNDLE_VAR, state.getBundleMethodSuffix(), label);
                } else {
                    builder.addStatement("$L.$L = $L.get$L($S)",
                            DESTINATION_VAR, label, BUNDLE_VAR, state.getBundleMethodSuffix(), label);
                }
                builder.endControlFlow();
            }
        }

        return builder.build();
    }

    protected MethodSpec supplyMethod() {
        return MethodSpec.methodBuilder(SUPPLY_METHOD)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(SUPPLIER_CLASS)
                .addStatement("return new $T()", SUPPLIER_CLASS)
                .build();
    }

    protected MethodSpec retrieveBundleMethod() {
        return MethodSpec.methodBuilder(RETRIEVE_METHOD)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(CommonClasses.BUNDLE, BUNDLE_VAR)
                .returns(RETRIEVER_CLASS)
                .beginControlFlow("if($L != null)", BUNDLE_VAR)
                .addStatement("return new $T($L)", RETRIEVER_CLASS, BUNDLE_VAR)
                .endControlFlow()
                .beginControlFlow("else")
                .addStatement("return null")
                .endControlFlow()
                .build();
    }

    protected List<MethodSpec> getAdditionalHelperMethods() {
        return new ArrayList<>();
    }

    public TypeSpec createKeysInterface() {
        TypeSpec.Builder keyBuilder = TypeSpec.interfaceBuilder(KEYS_SIMPLE_NAME)
                .addModifiers(Modifier.PUBLIC);
        for (AnnotatedField cargo : cargoList) {
            FieldSpec fieldSpec = FieldSpec.builder(String.class, cargo.getKeyConstant(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", cargo.getKeyConstant().toLowerCase())
                    .build();
            keyBuilder.addField(fieldSpec);
        }
        return keyBuilder.build();
    }

    private TypeSpec createSupplierClass() {
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();

        MethodSpec.Builder bundleBuilder = MethodSpec.methodBuilder(BUNDLE_METHOD)
                .addModifiers(Modifier.PUBLIC)
                .returns(CommonClasses.BUNDLE)
                .addStatement("$T $L = new $T()",
                        CommonClasses.BUNDLE,
                        BUNDLE_VAR,
                        CommonClasses.BUNDLE);

        TypeSpec.Builder builder = TypeSpec.classBuilder(SUPPLIER_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addMethod(constructor);

        TypeName type;
        String label;
        for (AnnotatedField cargo : cargoList) {
            type = cargo.getTypeName();
            label = cargo.getLabel();

            if (type.isPrimitive()) {
                builder.addField(type.box(), label, Modifier.PRIVATE);
            } else {
                builder.addField(type, label, Modifier.PRIVATE);
            }

            bundleBuilder.beginControlFlow("if($L != null)", label);
            bundleBuilder.addStatement("$L.put$L($T.$L, $L)",
                    BUNDLE_VAR,
                    cargo.getBundleMethodSuffix(),
                    KEYS_CLASS,
                    cargo.getKeyConstant(),
                    label);
            bundleBuilder.endControlFlow();

            builder.addMethod(supplierSetterMethod(type, label));
        }

        bundleBuilder.addStatement("return $L", BUNDLE_VAR);
        builder.addMethod(bundleBuilder.build());
        builder.addMethods(getAdditionalSupplierMethods());

        return builder.build();
    }

    protected List<MethodSpec> getAdditionalSupplierMethods() {
        return new ArrayList<>();
    }

    private MethodSpec supplierSetterMethod(TypeName type, String label) {
        return MethodSpec.methodBuilder(label)
                .addModifiers(Modifier.PUBLIC)
                .returns(SUPPLIER_CLASS)
                .addParameter(type, label)
                .addStatement("this.$L = $L", label, label)
                .addStatement("return this")
                .build();
    }

    private TypeSpec createRetrieverClass() {
        String HAS_PREFIX = "has";

        FieldSpec bundle = FieldSpec.builder(CommonClasses.BUNDLE, BUNDLE_VAR, Modifier.PRIVATE)
                .build();
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterSpec.builder(CommonClasses.BUNDLE, BUNDLE_VAR).build())
                .addStatement("this.$L = $L", BUNDLE_VAR, BUNDLE_VAR)
                .build();

        MethodSpec.Builder intoBuilder = MethodSpec.methodBuilder(INTO_METHOD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(freighterModel.getClassName(), DESTINATION_VAR);

        TypeSpec.Builder builder = TypeSpec.classBuilder(RETRIEVER_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addField(bundle)
                .addMethod(constructor);

        String label;
        TypeName type;
        String hasMethod;
        for (AnnotatedField cargo : cargoList) {
            label = cargo.getLabel();
            type = cargo.getTypeName();

            hasMethod = HAS_PREFIX + StringUtils.getClassName(label);
            builder.addMethod(retrieverHasMethod(hasMethod, cargo.getKeyConstant()));
            builder.addMethod(retrieverGetterMethod(type, label, hasMethod, cargo));

            intoBuilder.beginControlFlow("if($L())", hasMethod);
            if (type.isPrimitive()) {
                intoBuilder.addStatement("$L.$L = $L($L.$L)", DESTINATION_VAR, label,
                        label, DESTINATION_VAR, label);
            } else {
                intoBuilder.addStatement("$L.$L = $L()", DESTINATION_VAR, label, label);
            }
            intoBuilder.endControlFlow();
            // TODO throw exception in else block if @NotEmpty present
        }

        builder.addMethod(intoBuilder.build());
        return builder.build();
    }

    private MethodSpec retrieverHasMethod(String hasMethod, String intentKey) {
        return MethodSpec.methodBuilder(hasMethod)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return $L.containsKey($T.$L)", BUNDLE_VAR, KEYS_CLASS, intentKey)
                .build();
    }

    private MethodSpec retrieverGetterMethod(TypeName type, String label, String hasMethod, AnnotatedField cargo) {
        MethodSpec.Builder getterMethodBuilder = MethodSpec.methodBuilder(label)
                .addModifiers(Modifier.PUBLIC)
                .returns(type);

        if (type.isPrimitive()) {
            getterMethodBuilder.addParameter(type, DEFAULT_VAR);
        }

        if (type.isPrimitive()) {
            getterMethodBuilder.addStatement("return $L.get$L($T.$L, $L)",
                    BUNDLE_VAR,
                    cargo.getBundleMethodSuffix(),
                    KEYS_CLASS,
                    cargo.getKeyConstant(),
                    DEFAULT_VAR
            );
        } else if (cargo.requiresCasting()) {
            getterMethodBuilder.beginControlFlow("if($L())", hasMethod);
            getterMethodBuilder.addStatement("return ($T) $L.get$L($T.$L)",
                    type,
                    BUNDLE_VAR,
                    cargo.getBundleMethodSuffix(),
                    KEYS_CLASS,
                    cargo.getKeyConstant()
            );
            getterMethodBuilder.endControlFlow();
            getterMethodBuilder.addStatement("return null");
        } else {
            getterMethodBuilder.addStatement("return $L.get$L($T.$L)",
                    BUNDLE_VAR,
                    cargo.getBundleMethodSuffix(),
                    KEYS_CLASS,
                    cargo.getKeyConstant()
            );
        }


        return getterMethodBuilder.build();
    }
}
