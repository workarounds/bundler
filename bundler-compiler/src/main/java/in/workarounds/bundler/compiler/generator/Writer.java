package in.workarounds.bundler.compiler.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
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
import in.workarounds.bundler.compiler.util.StringUtils;
import in.workarounds.bundler.compiler.util.names.ClassProvider;
import in.workarounds.bundler.compiler.util.names.MethodName;
import in.workarounds.bundler.compiler.util.names.VarName;

/**
 * Created by madki on 19/10/15.
 */
public class Writer {
    protected Provider provider;
    protected ReqBundlerModel model;
    protected List<ArgModel> argList;
    protected List<StateModel> states;

    public static Writer from(Provider provider, ReqBundlerModel reqBundlerModel) {
        switch (reqBundlerModel.getVariety()) {
            case ACTIVITY:
                return new ActivityWriter(provider, reqBundlerModel);
            case SERVICE:
                return new ServiceWriter(provider, reqBundlerModel);
            case FRAGMENT:
            case FRAGMENT_V4:
                return new FragmentWriter(provider, reqBundlerModel);
            default:
                return new OtherWriter(provider, reqBundlerModel);
        }
    }


    protected Writer(Provider provider, ReqBundlerModel model) {
        this.provider = provider;
        this.model = model;
        this.argList = model.getArgs();
        this.states = model.getStates();
    }

    public TypeSpec.Builder addToBundler(TypeSpec.Builder classBuilder) {
        // save, restore
        classBuilder
                .addMethod(bundlerSaveMethod())
                .addMethod(bundlerRestoreMethod());
        // supply, retrieve
        classBuilder
                .addMethods(getAdditionalBundlerMethods());
        return classBuilder;
    }

    public JavaFile brewHelper() {
        TypeSpec helper = TypeSpec.classBuilder(ClassProvider.helper(model).simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addType(createBuilderClass())
                .addType(createParserClass())
                .addType(createKeysInterface())
                .addMethod(saveMethod())
                .addMethod(restoreMethod())
                .addMethod(buildMethod())
                .addMethod(parseBundleMethod())
                .addMethods(getAdditionalHelperMethods())
                .build();
        return JavaFile.builder(model.getPackageName(), helper).build();
    }

    protected MethodSpec bundlerSaveMethod() {
        return MethodSpec.methodBuilder(MethodName.saveState)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(model.getClassName(), VarName.from(model))
                .addParameter(ClassProvider.bundle, VarName.bundle)
                .returns(ClassProvider.bundle)
                .addStatement("return $T.$L($L, $L)",
                        ClassProvider.helper(model), MethodName.saveState,
                        VarName.from(model), VarName.bundle
                )
                .build();
    }

    protected MethodSpec bundlerRestoreMethod() {
        return MethodSpec.methodBuilder(MethodName.restoreState)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(model.getClassName(), VarName.from(model))
                .addParameter(ClassProvider.bundle, VarName.bundle)
                .addStatement("$T.$L($L, $L)",
                        ClassProvider.helper(model), MethodName.restoreState,
                        VarName.from(model), VarName.bundle
                )
                .build();
    }

    protected MethodSpec saveMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(MethodName.saveState)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassProvider.bundle)
                .addParameter(model.getClassName(), VarName.from(model))
                .addParameter(ClassProvider.bundle, VarName.bundle)
                .beginControlFlow("if($L == null)", VarName.bundle)
                .addStatement("$L = new $T()", VarName.bundle, ClassProvider.bundle)
                .endControlFlow();

        String label;
        TypeName type;
        for (StateModel state : states) {
            label = state.getLabel();
            type = state.getTypeName();

            if (type.isPrimitive()) {
                builder.addStatement("$L.put$L($S, $L.$L)",
                        VarName.bundle, state.getBundleMethodSuffix(), label, VarName.from(model), label);
            } else {
                builder.beginControlFlow("if($L.$L != null)", VarName.from(model), label)
                        .addStatement("$L.put$L($S, $L.$L)",
                                VarName.bundle, state.getBundleMethodSuffix(), label, VarName.from(model), label)
                        .endControlFlow();
            }
        }
        builder.addStatement("return $L", VarName.bundle);
        return builder.build();
    }

    protected MethodSpec restoreMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(MethodName.restoreState)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(model.getClassName(), VarName.from(model))
                .addParameter(ClassProvider.bundle, VarName.bundle)
                .beginControlFlow("if($L == null)", VarName.bundle)
                .addStatement("return")
                .endControlFlow();

        String label;
        TypeName type;
        for (StateModel state : states) {
            label = state.getLabel();
            type = state.getTypeName();

            if (type.isPrimitive()) {
                builder.addStatement("$L.$L = $L.get$L($S, $L.$L)",
                        VarName.from(model), label, VarName.bundle, state.getBundleMethodSuffix(), label, VarName.from(model), label);
            } else {
                builder.beginControlFlow("if($L.containsKey($S))", VarName.bundle, label);
                if (state.requiresCasting()) {
                    builder.addStatement("$L.$L = ($T) $L.get$L($S)",
                            VarName.from(model), label, type, VarName.bundle, state.getBundleMethodSuffix(), label);
                } else {
                    builder.addStatement("$L.$L = $L.get$L($S)",
                            VarName.from(model), label, VarName.bundle, state.getBundleMethodSuffix(), label);
                }
                builder.endControlFlow();
            }
        }

        return builder.build();
    }

    protected MethodSpec parseBundleMethod() {
        return MethodSpec.methodBuilder(MethodName.parse)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassProvider.bundle, VarName.bundle)
                .returns(ClassProvider.parser(model))
                .addStatement("return new $T($L)", ClassProvider.parser(model), VarName.bundle)
                .build();
    }

    protected MethodSpec buildMethod() {
        return MethodSpec.methodBuilder(MethodName.build)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassProvider.builder(model))
                .addStatement("return new $T()", ClassProvider.builder(model))
                .build();
    }

    protected List<MethodSpec> getAdditionalHelperMethods() {
        return new ArrayList<>();
    }

    protected List<MethodSpec> getAdditionalBundlerMethods() {
        return new ArrayList<>();
    }

    public TypeSpec createKeysInterface() {
        TypeSpec.Builder keyBuilder = TypeSpec.interfaceBuilder(ClassProvider.keys(model).simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
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
                .addModifiers(Modifier.PRIVATE)
                .build();

        MethodSpec.Builder bundleBuilder = MethodSpec.methodBuilder(MethodName.bundle)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassProvider.bundle)
                .addStatement("$T $L = new $T()",
                        ClassProvider.bundle,
                        VarName.bundle,
                        ClassProvider.bundle);

        TypeSpec.Builder builder = TypeSpec.classBuilder(ClassProvider.builder(model).simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
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
                    VarName.bundle,
                    cargo.getBundleMethodSuffix(),
                    ClassProvider.keys(model),
                    cargo.getKeyConstant(),
                    label);
            bundleBuilder.endControlFlow();

            builder.addMethod(builderSetterMethod(type, label, cargo.getSupportAnnotations()));
        }

        bundleBuilder.addStatement("return $L", VarName.bundle);
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
                .returns(ClassProvider.builder(model))
                .addParameter(ParameterSpec.builder(type, label).addAnnotations(annotationSpecs).build())
                .addStatement("this.$L = $L", label, label)
                .addStatement("return this")
                .build();
    }


    public TypeSpec createParserClass() {
        String HAS_PREFIX = "has";

        FieldSpec bundle = FieldSpec.builder(ClassProvider.bundle, VarName.bundle, Modifier.PRIVATE)
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterSpec.builder(ClassProvider.bundle, VarName.bundle).build())
                .addStatement("this.$L = $L", VarName.bundle, VarName.bundle)
                .build();

        MethodSpec isNull = MethodSpec.methodBuilder(MethodName.isNull)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return $L == null", VarName.bundle)
                .build();

        MethodSpec.Builder intoBuilder = MethodSpec.methodBuilder(MethodName.into)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(model.getClassName(), VarName.from(model));

        TypeSpec.Builder builder = TypeSpec.classBuilder(ClassProvider.parser(model).simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
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
                intoBuilder.addStatement("$L.$L = $L($L.$L)", VarName.from(model), label,
                        label, VarName.from(model), label);
            } else {
                intoBuilder.addStatement("$L.$L = $L()", VarName.from(model), label, label);
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
                .addStatement("return !$L() && $L.containsKey($T.$L)", MethodName.isNull, VarName.bundle, ClassProvider.keys(model), intentKey)
                .build();
    }

    private MethodSpec parserGetterMethod(TypeName type, String label, String hasMethod, ArgModel cargo) {
        MethodSpec.Builder getterMethodBuilder = MethodSpec.methodBuilder(label)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotations(cargo.getSupportAnnotations())
                .returns(type);


        if (type.isPrimitive()) {
            getterMethodBuilder.addParameter(type, VarName.defaultVal);
            getterMethodBuilder.beginControlFlow("if($L())", MethodName.isNull)
                    .addStatement("return $L", VarName.defaultVal)
                    .endControlFlow();
            getterMethodBuilder.addStatement("return $L.get$L($T.$L, $L)",
                    VarName.bundle,
                    cargo.getBundleMethodSuffix(),
                    ClassProvider.keys(model),
                    cargo.getKeyConstant(),
                    VarName.defaultVal
            );
        } else if (cargo.requiresCasting()) {
            getterMethodBuilder.beginControlFlow("if($L())", hasMethod);
            getterMethodBuilder.addStatement("return ($T) $L.get$L($T.$L)",
                    type,
                    VarName.bundle,
                    cargo.getBundleMethodSuffix(),
                    ClassProvider.keys(model),
                    cargo.getKeyConstant()
            );
            getterMethodBuilder.endControlFlow();
            getterMethodBuilder.addStatement("return null");
        } else {
            getterMethodBuilder.beginControlFlow("if($L())", MethodName.isNull)
                    .addStatement("return null")
                    .endControlFlow();
            getterMethodBuilder.addStatement("return $L.get$L($T.$L)",
                    VarName.bundle,
                    cargo.getBundleMethodSuffix(),
                    ClassProvider.keys(model),
                    cargo.getKeyConstant()
            );
        }


        return getterMethodBuilder.build();
    }
}
