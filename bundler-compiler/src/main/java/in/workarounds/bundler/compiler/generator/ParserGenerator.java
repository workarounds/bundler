package in.workarounds.bundler.compiler.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

import in.workarounds.bundler.compiler.model.ArgModel;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.util.StringUtils;
import in.workarounds.bundler.compiler.util.names.ClassProvider;
import in.workarounds.bundler.compiler.util.names.MethodName;
import in.workarounds.bundler.compiler.util.names.VarName;

/**
 * Created by madki on 17/11/15.
 */
public class ParserGenerator {
    private ReqBundlerModel model;

    public ParserGenerator(ReqBundlerModel model) {
        this.model = model;
    }

    public TypeSpec createClass() {
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
        for (ArgModel arg : model.getArgs()) {
            label = arg.getLabel();
            type = arg.getTypeName();

            hasMethod = HAS_PREFIX + StringUtils.getClassName(label);
            builder.addMethod(hasMethod(model, arg));
            builder.addMethod(getterMethod(model, arg));

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

    private MethodSpec hasMethod(ReqBundlerModel model, ArgModel arg) {
        return MethodSpec.methodBuilder(MethodName.has(arg))
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return !$L() && $L.containsKey($T.$L)",
                        MethodName.isNull,
                        VarName.bundle,
                        ClassProvider.keys(model),
                        arg.getKeyConstant())
                .build();
    }

    private MethodSpec getterMethod(ReqBundlerModel model, ArgModel arg) {
        TypeName type = arg.getTypeName();
        MethodSpec.Builder builder = MethodSpec.methodBuilder(arg.getLabel())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotations(arg.getSupportAnnotations())
                .returns(type);


        if (type.isPrimitive()) {
            builder.addParameter(type, VarName.defaultVal);
            builder.beginControlFlow("if($L())", MethodName.isNull)
                    .addStatement("return $L", VarName.defaultVal)
                    .endControlFlow();
            builder.addStatement("return $L.get$L($T.$L, $L)",
                    VarName.bundle,
                    arg.getBundleMethodSuffix(),
                    ClassProvider.keys(model),
                    arg.getKeyConstant(),
                    VarName.defaultVal
            );
        } else if (arg.requiresCasting()) {
            builder.beginControlFlow("if($L())", MethodName.has(arg));
            builder.addStatement("return ($T) $L.get$L($T.$L)",
                    type,
                    VarName.bundle,
                    arg.getBundleMethodSuffix(),
                    ClassProvider.keys(model),
                    arg.getKeyConstant()
            );
            builder.endControlFlow();
            builder.addStatement("return null");
        } else {
            builder.beginControlFlow("if($L())", MethodName.isNull)
                    .addStatement("return null")
                    .endControlFlow();
            builder.addStatement("return $L.get$L($T.$L)",
                    VarName.bundle,
                    arg.getBundleMethodSuffix(),
                    ClassProvider.keys(model),
                    arg.getKeyConstant()
            );
        }


        return builder.build();
    }


}
