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
import in.workarounds.bundler.compiler.util.CommonClasses;
import in.workarounds.bundler.compiler.util.StringUtils;

/**
 * Created by madki on 19/10/15.
 */
public class Writer {
    protected Provider provider;
    protected ReqBundlerModel model;
    protected List<ArgModel> argList;
    protected List<StateModel> states;

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


    protected Writer(Provider provider, ReqBundlerModel model, List<ArgModel> argList, List<StateModel> states, String packageName) {
        this.provider = provider;
        this.model = model;
        this.argList = argList;
        this.states = states;
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
        TypeSpec helper = TypeSpec.classBuilder(model.classes().helper().simpleName())
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
        return MethodSpec.methodBuilder(model.methods().saveState())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(model.getClassName(), model.vars().target())
                .addParameter(CommonClasses.BUNDLE, model.vars().bundle())
                .returns(CommonClasses.BUNDLE)
                .addStatement("return $T.$L($L, $L)",
                        model.classes().helper(), model.methods().saveState(),
                        model.vars().target(), model.vars().bundle()
                )
                .build();
    }

    protected MethodSpec bundlerRestoreMethod() {
        return MethodSpec.methodBuilder(model.methods().restoreState())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(model.getClassName(), model.vars().target())
                .addParameter(CommonClasses.BUNDLE, model.vars().bundle())
                .addStatement("$T.$L($L, $L)",
                        model.classes().helper(), model.methods().restoreState(),
                        model.vars().target(), model.vars().bundle()
                )
                .build();
    }

    protected MethodSpec saveMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(model.methods().saveState())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(CommonClasses.BUNDLE)
                .addParameter(model.getClassName(), model.vars().target())
                .addParameter(CommonClasses.BUNDLE, model.vars().bundle())
                .beginControlFlow("if($L == null)", model.vars().bundle())
                .addStatement("$L = new $T()", model.vars().bundle(), CommonClasses.BUNDLE)
                .endControlFlow();

        String label;
        TypeName type;
        for (StateModel state : states) {
            label = state.getLabel();
            type = state.getTypeName();

            if (type.isPrimitive()) {
                builder.addStatement("$L.put$L($S, $L.$L)",
                        model.vars().bundle(), state.getBundleMethodSuffix(), label, model.vars().target(), label);
            } else {
                builder.beginControlFlow("if($L.$L != null)", model.vars().target(), label)
                        .addStatement("$L.put$L($S, $L.$L)",
                                model.vars().bundle(), state.getBundleMethodSuffix(), label, model.vars().target(), label)
                        .endControlFlow();
            }
        }
        builder.addStatement("return $L", model.vars().bundle());
        return builder.build();
    }

    protected MethodSpec restoreMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(model.methods().restoreState())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(model.getClassName(), model.vars().target())
                .addParameter(CommonClasses.BUNDLE, model.vars().bundle())
                .beginControlFlow("if($L == null)", model.vars().bundle())
                .addStatement("return")
                .endControlFlow();

        String label;
        TypeName type;
        for (StateModel state : states) {
            label = state.getLabel();
            type = state.getTypeName();

            if (type.isPrimitive()) {
                builder.addStatement("$L.$L = $L.get$L($S, $L.$L)",
                        model.vars().target(), label, model.vars().bundle(), state.getBundleMethodSuffix(), label, model.vars().target(), label);
            } else {
                builder.beginControlFlow("if($L.containsKey($S))", model.vars().bundle(), label);
                if (state.requiresCasting()) {
                    builder.addStatement("$L.$L = ($T) $L.get$L($S)",
                            model.vars().target(), label, type, model.vars().bundle(), state.getBundleMethodSuffix(), label);
                } else {
                    builder.addStatement("$L.$L = $L.get$L($S)",
                            model.vars().target(), label, model.vars().bundle(), state.getBundleMethodSuffix(), label);
                }
                builder.endControlFlow();
            }
        }

        return builder.build();
    }

    protected MethodSpec parseBundleMethod() {
        return MethodSpec.methodBuilder(model.methods().parse())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(CommonClasses.BUNDLE, model.vars().bundle())
                .returns(model.classes().parser())
                .addStatement("return new $T($L)", model.classes().parser(), model.vars().bundle())
                .build();
    }

    protected MethodSpec buildMethod() {
        return MethodSpec.methodBuilder(model.methods().build())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(model.classes().builder())
                .addStatement("return new $T()", model.classes().builder())
                .build();
    }

    protected List<MethodSpec> getAdditionalHelperMethods() {
        return new ArrayList<>();
    }

    protected List<MethodSpec> getAdditionalBundlerMethods() {
        return new ArrayList<>();
    }

    public TypeSpec createKeysInterface() {
        TypeSpec.Builder keyBuilder = TypeSpec.interfaceBuilder(model.classes().keys().simpleName())
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

        MethodSpec.Builder bundleBuilder = MethodSpec.methodBuilder(model.methods().bundle())
                .addModifiers(Modifier.PUBLIC)
                .returns(CommonClasses.BUNDLE)
                .addStatement("$T $L = new $T()",
                        CommonClasses.BUNDLE,
                        model.vars().bundle(),
                        CommonClasses.BUNDLE);

        TypeSpec.Builder builder = TypeSpec.classBuilder(model.classes().builder().simpleName())
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
                    model.vars().bundle(),
                    cargo.getBundleMethodSuffix(),
                    model.classes().keys(),
                    cargo.getKeyConstant(),
                    label);
            bundleBuilder.endControlFlow();

            builder.addMethod(builderSetterMethod(type, label, cargo.getSupportAnnotations()));
        }

        bundleBuilder.addStatement("return $L", model.vars().bundle());
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
                .returns(model.classes().builder())
                .addParameter(ParameterSpec.builder(type, label).addAnnotations(annotationSpecs).build())
                .addStatement("this.$L = $L", label, label)
                .addStatement("return this")
                .build();
    }


    public TypeSpec createParserClass() {
        String HAS_PREFIX = "has";

        FieldSpec bundle = FieldSpec.builder(CommonClasses.BUNDLE, model.vars().bundle(), Modifier.PRIVATE)
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterSpec.builder(CommonClasses.BUNDLE, model.vars().bundle()).build())
                .addStatement("this.$L = $L", model.vars().bundle(), model.vars().bundle())
                .build();

        MethodSpec isNull = MethodSpec.methodBuilder(model.methods().isNull())
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return $L == null", model.vars().bundle())
                .build();

        MethodSpec.Builder intoBuilder = MethodSpec.methodBuilder(model.methods().into())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(model.getClassName(), model.vars().target());

        TypeSpec.Builder builder = TypeSpec.classBuilder(model.classes().parser().simpleName())
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
                intoBuilder.addStatement("$L.$L = $L($L.$L)", model.vars().target(), label,
                        label, model.vars().target(), label);
            } else {
                intoBuilder.addStatement("$L.$L = $L()", model.vars().target(), label, label);
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
                .addStatement("return !$L() && $L.containsKey($T.$L)", model.methods().isNull(), model.vars().bundle(), model.classes().keys(), intentKey)
                .build();
    }

    private MethodSpec parserGetterMethod(TypeName type, String label, String hasMethod, ArgModel cargo) {
        MethodSpec.Builder getterMethodBuilder = MethodSpec.methodBuilder(label)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotations(cargo.getSupportAnnotations())
                .returns(type);


        if (type.isPrimitive()) {
            getterMethodBuilder.addParameter(type, model.vars().defaultVal());
            getterMethodBuilder.beginControlFlow("if($L())", model.methods().isNull())
                    .addStatement("return $L", model.vars().defaultVal())
                    .endControlFlow();
            getterMethodBuilder.addStatement("return $L.get$L($T.$L, $L)",
                    model.vars().bundle(),
                    cargo.getBundleMethodSuffix(),
                    model.classes().keys(),
                    cargo.getKeyConstant(),
                    model.vars().defaultVal()
            );
        } else if (cargo.requiresCasting()) {
            getterMethodBuilder.beginControlFlow("if($L())", hasMethod);
            getterMethodBuilder.addStatement("return ($T) $L.get$L($T.$L)",
                    type,
                    model.vars().bundle(),
                    cargo.getBundleMethodSuffix(),
                    model.classes().keys(),
                    cargo.getKeyConstant()
            );
            getterMethodBuilder.endControlFlow();
            getterMethodBuilder.addStatement("return null");
        } else {
            getterMethodBuilder.beginControlFlow("if($L())", model.methods().isNull())
                    .addStatement("return null")
                    .endControlFlow();
            getterMethodBuilder.addStatement("return $L.get$L($T.$L)",
                    model.vars().bundle(),
                    cargo.getBundleMethodSuffix(),
                    model.classes().keys(),
                    cargo.getKeyConstant()
            );
        }


        return getterMethodBuilder.build();
    }
}
