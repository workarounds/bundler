package in.workarounds.bundler.compiler.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.model.ArgModel;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.model.StateModel;
import in.workarounds.bundler.compiler.util.CommonClasses;
import in.workarounds.bundler.compiler.util.StringUtils;

/**
 * Created by madki on 19/10/15.
 */
public class Writer {
    protected Provider provider;
    protected ReqBundlerModel reqBundlerModel;
    protected List<ArgModel> argList;
    protected List<StateModel> states;
    public static final String FILE_SIMPLE_NAME = "Bundler";
    protected String KEYS_SIMPLE_NAME = "Keys";
    protected String BUILDER_NAME = "$$Builder";
    protected String PARSER_NAME = "$$Parser";
    protected String BUILD_METHOD = "";
    protected String PARSE_METHOD = "parse";
    protected static final String INTO_METHOD = "into";
    protected static final String BUNDLE_METHOD = "bundle";
    protected static final String INTENT_METHOD = "intent";
    protected static final String START_METHOD = "start";
    protected static final String CREATE_METHOD = "create";
    protected static final String INJECT_METHOD = "inject";
    protected static final String SAVE_METHOD = "saveState";
    protected static final String RESTORE_METHOD = "restoreState";
    protected static final String IS_NULL_METHOD = "isNull";
    protected static final String RETRIEVER_VAR = "retriever";

    protected String bundlerPackageName;
    protected String DESTINATION_VAR;
    protected ClassName SUPPLIER_CLASS;
    protected ClassName RETRIEVER_CLASS;

    protected ClassName KEYS_CLASS;

    protected String CONTEXT_VAR = "context";
    protected String BUNDLE_VAR = "bundle";
    protected String DEFAULT_VAR = "defaultValue";
    protected static final String INTENT_VAR = "intent";

    public static Writer from(Provider provider, ReqBundlerModel reqBundlerModel, List<ArgModel> cargoList, List<StateModel> states, String packageName) {
        switch (reqBundlerModel.getVariety()) {
            case ACTIVITY:
                return new ActivityWriter(provider, reqBundlerModel, cargoList, states, packageName);
            case SERVICE:
                return new ServiceWriter(provider, reqBundlerModel, cargoList, states, packageName);
            case FRAGMENT:
            case FRAGMENT_V4:
                return new FragmentWriter(provider, reqBundlerModel, cargoList, states, packageName);
            default:
                return new OtherWriter(provider, reqBundlerModel, cargoList, states, packageName);
        }
    }


    protected Writer(Provider provider, ReqBundlerModel reqBundlerModel, List<ArgModel> argList, List<StateModel> states, String packageName) {
        this.provider = provider;
        this.reqBundlerModel = reqBundlerModel;
        this.argList = argList;
        this.states = states;
        this.bundlerPackageName = packageName;

        DESTINATION_VAR = StringUtils.getVariableName(reqBundlerModel.getSimpleName());

        BUILD_METHOD = DESTINATION_VAR;
        PARSE_METHOD = PARSE_METHOD + reqBundlerModel.getSimpleName();
        BUILDER_NAME = reqBundlerModel.getSimpleName() + BUILDER_NAME;
        PARSER_NAME = reqBundlerModel.getSimpleName() + PARSER_NAME;

        KEYS_SIMPLE_NAME = KEYS_SIMPLE_NAME + reqBundlerModel.getSimpleName();

        String FILE_NAME = bundlerPackageName + "." + FILE_SIMPLE_NAME;

        SUPPLIER_CLASS = ClassName.bestGuess(reqBundlerModel.getPackageName() + "." + BUILDER_NAME);
        RETRIEVER_CLASS = ClassName.bestGuess(reqBundlerModel.getPackageName() + "." + PARSER_NAME);
        KEYS_CLASS = ClassName.bestGuess(FILE_NAME + "." + KEYS_SIMPLE_NAME);

    }

    public TypeSpec.Builder addToBundler(TypeSpec.Builder classBuilder) {
        // save, restore
        classBuilder
                .addMethod(saveMethod())
                .addMethod(restoreMethod());
        // supply, retrieve
        classBuilder
                .addMethod(buildMethod())
                .addMethod(parseBundleMethod())
                .addMethods(getAdditionalHelperMethods())
                .addType(createKeysInterface());
        return classBuilder;
    }

    protected MethodSpec saveMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(SAVE_METHOD)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(reqBundlerModel.getClassName(), DESTINATION_VAR)
                .addParameter(CommonClasses.BUNDLE, BUNDLE_VAR)
                .beginControlFlow("if($L == null)", BUNDLE_VAR)
                .addStatement("$L = new $T()", BUNDLE_VAR, CommonClasses.BUNDLE)
                .endControlFlow();

        String label;
        TypeName type;
        for (StateModel state : states) {
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
                .addParameter(reqBundlerModel.getClassName(), DESTINATION_VAR)
                .addParameter(CommonClasses.BUNDLE, BUNDLE_VAR)
                .beginControlFlow("if($L == null)", BUNDLE_VAR)
                .addStatement("return")
                .endControlFlow();

        String label;
        TypeName type;
        for (StateModel state : states) {
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

    protected MethodSpec buildMethod() {
        return MethodSpec.methodBuilder(BUILD_METHOD)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(SUPPLIER_CLASS)
                .addStatement("return new $T()", SUPPLIER_CLASS)
                .build();
    }

    protected MethodSpec parseBundleMethod() {
        return MethodSpec.methodBuilder(PARSE_METHOD)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(CommonClasses.BUNDLE, BUNDLE_VAR)
                .returns(RETRIEVER_CLASS)
                .addStatement("return new $T($L)", RETRIEVER_CLASS, BUNDLE_VAR)
                .build();
    }

    protected List<MethodSpec> getAdditionalHelperMethods() {
        return new ArrayList<>();
    }

    public TypeSpec createKeysInterface() {
        TypeSpec.Builder keyBuilder = TypeSpec.interfaceBuilder(KEYS_SIMPLE_NAME)
                .addModifiers(Modifier.PUBLIC);
        for (ArgModel cargo : argList) {
            FieldSpec fieldSpec = FieldSpec.builder(String.class, cargo.getKeyConstant(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", cargo.getKeyConstant().toLowerCase())
                    .build();
            keyBuilder.addField(fieldSpec);
        }
        return keyBuilder.build();
    }

    public TypeSpec createBuilderClass() {
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();

        MethodSpec.Builder bundleBuilder = MethodSpec.methodBuilder(BUNDLE_METHOD)
                .addModifiers(Modifier.PUBLIC)
                .returns(CommonClasses.BUNDLE)
                .addStatement("$T $L = new $T()",
                        CommonClasses.BUNDLE,
                        BUNDLE_VAR,
                        CommonClasses.BUNDLE);

        TypeSpec.Builder builder = TypeSpec.classBuilder(BUILDER_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor);

        TypeName type;
        String label;
        for (ArgModel cargo : argList) {
            type = cargo.getTypeName();
            label = cargo.getLabel();

            FieldSpec.Builder fieldBuilder;
            if (type.isPrimitive()) {
                fieldBuilder = FieldSpec.builder(type.box(), label, Modifier.PRIVATE);
            } else {
                fieldBuilder = FieldSpec.builder(type, label, Modifier.PRIVATE);
            }
            fieldBuilder.addAnnotations(cargo.getSupportAnnotations());
            builder.addField(fieldBuilder.build());

            bundleBuilder.beginControlFlow("if($L != null)", label);
            bundleBuilder.addStatement("$L.put$L($T.$L, $L)",
                    BUNDLE_VAR,
                    cargo.getBundleMethodSuffix(),
                    KEYS_CLASS,
                    cargo.getKeyConstant(),
                    label);
            bundleBuilder.endControlFlow();

            builder.addMethod(builderSetterMethod(type, label, cargo.getSupportAnnotations()));
        }

        bundleBuilder.addStatement("return $L", BUNDLE_VAR);
        builder.addMethod(bundleBuilder.build());
        builder.addMethods(getAdditionalSupplierMethods());

        return builder.build();
    }

    protected List<MethodSpec> getAdditionalSupplierMethods() {
        return new ArrayList<>();
    }

    private MethodSpec builderSetterMethod(TypeName type, String label, List<AnnotationSpec> annotationSpecs) {
        return MethodSpec.methodBuilder(label)
                .addModifiers(Modifier.PUBLIC)
                .returns(SUPPLIER_CLASS)
                .addParameter(ParameterSpec.builder(type, label).addAnnotations(annotationSpecs).build())
                .addStatement("this.$L = $L", label, label)
                .addStatement("return this")
                .build();
    }

    public TypeSpec createParserClass() {
        String HAS_PREFIX = "has";

        FieldSpec bundle = FieldSpec.builder(CommonClasses.BUNDLE, BUNDLE_VAR, Modifier.PRIVATE)
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(CommonClasses.BUNDLE, BUNDLE_VAR).build())
                .addStatement("this.$L = $L", BUNDLE_VAR, BUNDLE_VAR)
                .build();

        MethodSpec isNull = MethodSpec.methodBuilder(IS_NULL_METHOD)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return $L == null", BUNDLE_VAR)
                .build();

        MethodSpec.Builder intoBuilder = MethodSpec.methodBuilder(INTO_METHOD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(reqBundlerModel.getClassName(), DESTINATION_VAR);

        TypeSpec.Builder builder = TypeSpec.classBuilder(PARSER_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addField(bundle)
                .addMethod(constructor)
                .addMethod(isNull);

        String label;
        TypeName type;
        String hasMethod;
        for (ArgModel cargo : argList) {
            label = cargo.getLabel();
            type = cargo.getTypeName();

            hasMethod = HAS_PREFIX + StringUtils.getClassName(label);
            builder.addMethod(parserHasMethod(hasMethod, cargo.getKeyConstant()));
            builder.addMethod(parserGetterMethod(type, label, hasMethod, cargo));

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

    private MethodSpec parserHasMethod(String hasMethod, String intentKey) {
        return MethodSpec.methodBuilder(hasMethod)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return !$L() && $L.containsKey($T.$L)", IS_NULL_METHOD, BUNDLE_VAR, KEYS_CLASS, intentKey)
                .build();
    }

    private MethodSpec parserGetterMethod(TypeName type, String label, String hasMethod, ArgModel cargo) {
        MethodSpec.Builder getterMethodBuilder = MethodSpec.methodBuilder(label)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotations(cargo.getSupportAnnotations())
                .returns(type);


        if (type.isPrimitive()) {
            getterMethodBuilder.addParameter(type, DEFAULT_VAR);
            getterMethodBuilder.beginControlFlow("if($L())", IS_NULL_METHOD)
                    .addStatement("return $L", DEFAULT_VAR)
                    .endControlFlow();
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
            getterMethodBuilder.beginControlFlow("if($L())", IS_NULL_METHOD)
                    .addStatement("return null")
                    .endControlFlow();
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
