package in.workarounds.bundler.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.bundler.compiler.model.ArgModel;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.util.names.ClassProvider;
import in.workarounds.bundler.compiler.util.names.MethodName;
import in.workarounds.bundler.compiler.util.names.VarName;

/**
 * Created by madki on 17/11/15.
 */
public class BuilderGenerator {
    private ReqBundlerModel model;

    public BuilderGenerator(ReqBundlerModel model) {
        this.model = model;
    }

    public TypeSpec createClass() {
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();


        TypeSpec.Builder builder = TypeSpec.classBuilder(ClassProvider.builder(model).simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addMethod(constructor);

        for (ArgModel arg : model.getArgs()) {
            builder.addField(arg.getAsField(Modifier.PRIVATE));
            builder.addMethod(setterMethod(model, arg));
        }

        builder.addMethod(bundleMethod());
        builder.addMethods(additionalMethods());

        return builder.build();
    }

    private MethodSpec setterMethod(ReqBundlerModel model, ArgModel arg) {
        return MethodSpec.methodBuilder(arg.getLabel())
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassProvider.builder(model))
                .addParameter(arg.getAsParameter())
                .addStatement("this.$1L = $1L", arg.getLabel())
                .addStatement("return this")
                .build();
    }

    protected List<MethodSpec> additionalMethods() {
        switch (model.getVariety()) {
            case ACTIVITY:
            case SERVICE:
                String methodName = model.getVariety() == ReqBundlerModel.VARIETY.ACTIVITY ?
                        "startActivity" : "startService";
                return Arrays.asList(intentMethod(), startMethod(methodName));
            case FRAGMENT:
            case FRAGMENT_V4:
                return Arrays.asList(createMethod());
            case OTHER:
            default:
                return new ArrayList<>();
        }
    }

    private MethodSpec bundleMethod() {
        MethodSpec.Builder bundleBuilder = MethodSpec.methodBuilder(MethodName.bundle)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassProvider.bundle)
                .addStatement("$T $L = new $T()",
                        ClassProvider.bundle,
                        VarName.bundle,
                        ClassProvider.bundle);

        for (ArgModel arg : model.getArgs()) {
            bundleBuilder.beginControlFlow("if($L != null)", arg.getLabel());
            ClassName serializer = arg.getSerializer();
            if(serializer != null) {
               bundleBuilder.addStatement("$L.put($T.$L, $L, $L)",
                       VarName.from(serializer),
                       ClassProvider.keys(model),
                       arg.getKeyConstant(),
                       arg.getLabel(),
                       VarName.bundle);
            } else {
                bundleBuilder.addStatement("$L.put$L($T.$L, $L)",
                        VarName.bundle,
                        arg.getBundleMethodSuffix(),
                        ClassProvider.keys(model),
                        arg.getKeyConstant(),
                        arg.getLabel());
            }
            bundleBuilder.endControlFlow();
        }

        bundleBuilder.addStatement("return $L", VarName.bundle);
        return bundleBuilder.build();
    }

    private MethodSpec intentMethod() {
        return MethodSpec.methodBuilder(MethodName.intent)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassProvider.context, VarName.context)
                .returns(ClassProvider.intent)
                .addStatement("$T $L = new $T($L, $T.class)",
                        ClassProvider.intent, VarName.intent, ClassProvider.intent,
                        VarName.context, model.getClassName())
                .addStatement("$L.putExtras($L())", VarName.intent, MethodName.bundle)
                .addStatement("return $L", VarName.intent)
                .build();
    }

    private MethodSpec startMethod(String methodName) {
        return MethodSpec.methodBuilder(MethodName.start)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassProvider.context, VarName.context)
                .addStatement("$L.$L($L($L))",
                        VarName.context,
                        methodName,
                        MethodName.intent,
                        VarName.context)
                .build();
    }

    private MethodSpec createMethod() {
        return MethodSpec.methodBuilder(MethodName.create)
                .addModifiers(Modifier.PUBLIC)
                .returns(model.getClassName())
                .addStatement("$T $L = new $T()", model.getClassName(), VarName.from(model), model.getClassName())
                .addStatement("$L.setArguments($L())", VarName.from(model), VarName.bundle)
                .addStatement("return $L", VarName.from(model))
                .build();
    }

}
