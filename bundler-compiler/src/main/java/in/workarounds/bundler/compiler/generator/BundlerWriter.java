package in.workarounds.bundler.compiler.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

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
public class BundlerWriter {
    private List<ReqBundlerModel> models;

    public BundlerWriter(List<ReqBundlerModel> models) {
        this.models = models;
    }

    public JavaFile brewJava() {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(ClassProvider.bundler.simpleName())
                .addModifiers(Modifier.PUBLIC);

        // TODO check data validity, same method names?

        for (ReqBundlerModel model: models) {
            classBuilder
                    .addMethod(injectMethod(model))
                    .addMethod(buildMethod(model, model.getRequiredArgs()))
                    .addMethod(saveMethod(model))
                    .addMethod(restoreMethod(model));
        }

        return JavaFile.builder(ClassProvider.bundler.packageName(), classBuilder.build()).build();
     }

    protected MethodSpec saveMethod(ReqBundlerModel model) {
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

    protected MethodSpec restoreMethod(ReqBundlerModel model) {
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

    protected MethodSpec injectMethod(ReqBundlerModel model) {
        switch (model.getVariety()) {
            case ACTIVITY:
            case FRAGMENT:
            case FRAGMENT_V4:
                String getMethodName = model.getVariety() == ReqBundlerModel.VARIETY.ACTIVITY?
                        "getIntent" : "getArguments";
                return MethodSpec.methodBuilder(MethodName.inject)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(model.getClassName(), VarName.from(model))
                        .addStatement("$T $L = $T.$L($L.$L())",
                                ClassProvider.parser(model),
                                VarName.parser, ClassProvider.helper(model),
                                MethodName.parse, VarName.from(model), getMethodName)
                        .beginControlFlow("if($L.$L())", VarName.parser, MethodName.isNull)
                        .addStatement("$L.$L($L)", VarName.parser, MethodName.into, VarName.from(model))
                        .endControlFlow()
                        .build();
            case SERVICE:
            case OTHER:
            default:
                return MethodSpec.methodBuilder(MethodName.inject)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(model.getClassName(), VarName.from(model))
                        .addParameter(ClassProvider.bundle, VarName.bundle)
                        .addStatement("$T $L = $T.$L($L)", ClassProvider.parser(model), VarName.parser, ClassProvider.helper(model), MethodName.parse, VarName.bundle)
                        .beginControlFlow("if($L.$L())", VarName.parser, MethodName.isNull)
                        .addStatement("$L.$L($L)", VarName.parser, MethodName.into, VarName.from(model))
                        .endControlFlow()
                        .build();
        }
    }

    protected MethodSpec buildMethod(ReqBundlerModel model, List<ArgModel> args) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(MethodName.build(model))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassProvider.builder(model));

        for (ArgModel arg : args) {
            builder.addParameter(arg.getAsParameter());
        }

        String statement = "return $T.$L()";
        for (ArgModel arg : args) {
            statement = statement + String.format(".%s(%s)", arg.getLabel(), arg.getLabel());
        }

        builder.addStatement(statement, ClassProvider.helper(model), MethodName.build);
        return builder.build();
    }

}
