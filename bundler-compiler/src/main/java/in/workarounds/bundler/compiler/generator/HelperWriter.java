package in.workarounds.bundler.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;

import in.workarounds.bundler.compiler.model.AnnotatedField;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.model.StateModel;
import in.workarounds.bundler.compiler.util.names.ClassProvider;
import in.workarounds.bundler.compiler.util.names.MethodName;
import in.workarounds.bundler.compiler.util.names.VarName;

/**
 * Created by madki on 19/10/15.
 */
public class HelperWriter {
    private ReqBundlerModel model;

    private BuilderGenerator builderGenerator;
    private ParserGenerator parserGenerator;
    private KeysGenerator keysGenerator;

    public HelperWriter(ReqBundlerModel model) {
        this.model = model;

        builderGenerator = new BuilderGenerator(model);
        parserGenerator = new ParserGenerator(model);
        keysGenerator = new KeysGenerator(model);
    }


    public JavaFile brewJava() {
        TypeSpec helper = TypeSpec.classBuilder(ClassProvider.helper(model).simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addField(getTagField())
                .addFields(getSerializerFields())
                .addType(builderGenerator.createClass())
                .addType(parserGenerator.createClass())
                .addType(keysGenerator.createKeysInterface())
                .addMethod(saveMethod())
                .addMethod(restoreMethod())
                .addMethod(buildMethod())
                .addMethod(parseBundleMethod())
                .addMethods(additionalMethods())
                .build();
        return JavaFile.builder(model.getPackageName(), helper).build();
    }

    private FieldSpec getTagField() {
        return FieldSpec.builder(
                String.class,
                VarName.tag,
                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL
        ).initializer("$S", ClassProvider.helper(model).simpleName())
                .build();
    }

    private List<FieldSpec> getSerializerFields() {
        Set<ClassName> serializers = new HashSet<>();

        for (AnnotatedField field : model.getArgs()) {
            if (field.getSerializer() != null) {
                serializers.add(field.getSerializer());
            }
        }

        for (AnnotatedField field : model.getArgs()) {
            if (field.getSerializer() != null) {
                serializers.add(field.getSerializer());
            }
        }

        List<FieldSpec> serializerFields = new ArrayList<>(serializers.size());

        for (ClassName serializer : serializers) {
            FieldSpec field = FieldSpec.builder(
                    serializer,
                    VarName.from(serializer),
                    Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL
            )
                    .initializer("new $T()", serializer)
                    .build();
            serializerFields.add(field);
        }

        return serializerFields;
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
        for (StateModel state : model.getStates()) {
            label = state.getLabel();
            type = state.getTypeName();

            if (type.isPrimitive()) {
                builder.addStatement("$L.put$L($S, $L.$L)",
                        VarName.bundle, state.getBundleMethodSuffix(), label, VarName.from(model), label);
            } else {
                builder.beginControlFlow("if($L.$L != null)", VarName.from(model), label);
                ClassName serializer = state.getSerializer();
                if (serializer != null) {
                    builder.addStatement("$L.put($S, $L.$L, $L)",
                            VarName.from(serializer),
                            label,
                            VarName.from(model),
                            label,
                            VarName.bundle);
                } else {
                    builder.addStatement("$L.put$L($S, $L.$L)",
                            VarName.bundle, state.getBundleMethodSuffix(), label, VarName.from(model), label);
                }
                builder.endControlFlow();
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
        for (StateModel state : model.getStates()) {
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
                    ClassName serializer = state.getSerializer();
                    if (serializer != null) {
                        builder.addStatement("$L.$L = $L.get($S, $L)",
                                VarName.from(model),
                                label,
                                VarName.from(serializer),
                                label,
                                VarName.bundle);
                    } else {
                        builder.addStatement("$L.$L = $L.get$L($S)",
                                VarName.from(model), label, VarName.bundle, state.getBundleMethodSuffix(), label);
                    }
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

    protected List<MethodSpec> additionalMethods() {
        switch (model.getVariety()) {
            case ACTIVITY:
            case SERVICE:
                return Arrays.asList(parseIntentMethod());
            case FRAGMENT:
            case FRAGMENT_V4:
            case OTHER:
            default:
                return new ArrayList<>();
        }
    }

    private MethodSpec parseIntentMethod() {
        return MethodSpec.methodBuilder(MethodName.parse)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassProvider.intent, VarName.intent)
                .returns(ClassProvider.parser(model))
                .beginControlFlow("if($L == null)", VarName.intent)
                .addStatement("return new $T(null)", ClassProvider.parser(model))
                .endControlFlow()
                .addStatement("return $L($L.getExtras())", MethodName.parse, VarName.intent)
                .build();
    }
}
