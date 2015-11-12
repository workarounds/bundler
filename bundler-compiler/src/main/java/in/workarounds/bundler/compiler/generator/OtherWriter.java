package in.workarounds.bundler.compiler.generator;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.model.ArgModel;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.model.StateModel;
import in.workarounds.bundler.compiler.util.CommonClasses;

/**
 * Created by madki on 25/10/15.
 */
public class OtherWriter extends Writer {
    protected static final String BUNDLER_VAR = "bundler";

    protected OtherWriter(Provider provider, ReqBundlerModel reqBundlerModel, List<ArgModel> cargoList, List<StateModel> states, String packageName) {
        super(provider, reqBundlerModel, cargoList, states, packageName);
    }


@Override
    protected List<MethodSpec> getAdditionalBundlerMethods() {
        List<MethodSpec> methods = super.getAdditionalBundlerMethods();
        methods.add(
                MethodSpec.methodBuilder(model.methods().inject())
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(model.getClassName(), BUNDLER_VAR)
                        .addParameter(CommonClasses.BUNDLE, model.vars().bundle())
                        .addStatement("$T $L = $T.$L($L)", model.classes().parser(), model.vars().parser(), model.classes().helper(), model.methods().parse(), model.vars().bundle())
                        .beginControlFlow("if($L.$L())", model.vars().parser(), model.methods().isNull())
                        .addStatement("$L.$L($L)", model.vars().parser(), model.methods().into(), BUNDLER_VAR)
                        .endControlFlow()
                        .build()
        );
        return methods;
    }}
