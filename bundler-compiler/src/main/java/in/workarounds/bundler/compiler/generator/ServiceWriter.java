package in.workarounds.bundler.compiler.generator;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.model.ArgModel;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.model.StateModel;
import in.workarounds.bundler.compiler.util.names.ClassProvider;
import in.workarounds.bundler.compiler.util.names.MethodName;
import in.workarounds.bundler.compiler.util.names.VarName;

/**
 * Created by madki on 24/10/15.
 */
public class ServiceWriter extends Writer {

    protected ServiceWriter(Provider provider, ReqBundlerModel reqBundlerModel, List<ArgModel> cargoList, List<StateModel> states) {
        super(provider, reqBundlerModel, cargoList, states);
    }

    @Override
    protected List<MethodSpec> getAdditionalHelperMethods() {
        List<MethodSpec> methods = super.getAdditionalHelperMethods();
        methods.add(
                MethodSpec.methodBuilder(MethodName.parse)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(ClassProvider.intent, VarName.intent)
                        .returns(ClassProvider.parser(model))
                        .beginControlFlow("if($L == null)", VarName.intent)
                        .addStatement("return new $T(null)", ClassProvider.parser(model))
                        .endControlFlow()
                        .addStatement("return $L($L.getExtras())", MethodName.parse, VarName.intent)
                        .build()
        );

        return methods;
    }

    @Override
    protected List<MethodSpec> getAdditionalSupplierMethods() {
        List<MethodSpec> methods = super.getAdditionalSupplierMethods();
        methods.add(
                MethodSpec.methodBuilder(MethodName.intent)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassProvider.context, VarName.context)
                        .returns(ClassProvider.intent)
                        .addStatement("$T $L = new $T($L, $T.class)", ClassProvider.intent, VarName.intent, ClassProvider.intent, VarName.context, model.getClassName())
                        .addStatement("$L.putExtras($L())", VarName.intent, MethodName.bundle)
                        .addStatement("return $L", VarName.intent)
                        .build()
        );
        methods.add(
                MethodSpec.methodBuilder(MethodName.start)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassProvider.context, VarName.context)
                        .addStatement("$L.startService($L($L))", VarName.context, MethodName.intent, VarName.context)
                        .build()
        );
        return methods;
    }

    @Override
    protected List<MethodSpec> getAdditionalBundlerMethods() {
        List<MethodSpec> methods = super.getAdditionalBundlerMethods();
        methods.add(
                MethodSpec.methodBuilder(MethodName.inject)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(model.getClassName(), VarName.from(model))
                        .addParameter(ClassProvider.intent, VarName.intent)
                        .addStatement("$T $L = $T.$L($L)", ClassProvider.parser(model), VarName.parser, ClassProvider.helper(model), MethodName.parse, VarName.intent)
                        .beginControlFlow("if(!$L.$L())", VarName.parser, MethodName.isNull)
                        .addStatement("$L.$L($L)", VarName.parser, MethodName.into, VarName.from(model))
                        .endControlFlow()
                        .build()
        );
        return methods;
    }
}
