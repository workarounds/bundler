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
 * Created by madki on 25/10/15.
 */
public class OtherWriter extends Writer {

    protected OtherWriter(Provider provider, ReqBundlerModel reqBundlerModel, List<ArgModel> cargoList, List<StateModel> states) {
        super(provider, reqBundlerModel, cargoList, states);
    }


@Override
    protected List<MethodSpec> getAdditionalBundlerMethods() {
        List<MethodSpec> methods = super.getAdditionalBundlerMethods();
        methods.add(
                MethodSpec.methodBuilder(MethodName.inject)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(model.getClassName(), VarName.from(model))
                        .addParameter(ClassProvider.bundle, VarName.bundle)
                        .addStatement("$T $L = $T.$L($L)", ClassProvider.parser(model), VarName.parser, ClassProvider.helper(model), MethodName.parse, VarName.bundle)
                        .beginControlFlow("if($L.$L())", VarName.parser, MethodName.isNull)
                        .addStatement("$L.$L($L)", VarName.parser, MethodName.into, VarName.from(model))
                        .endControlFlow()
                        .build()
        );
        return methods;
    }}
