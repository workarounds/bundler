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
    protected List<MethodSpec> getAdditionalHelperMethods() {
        List<MethodSpec> methods = super.getAdditionalHelperMethods();
        methods.add(
                MethodSpec.methodBuilder(INJECT_METHOD)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(reqBundlerModel.getClassName(), BUNDLER_VAR)
                        .addParameter(CommonClasses.BUNDLE, BUNDLE_VAR)
                        .addStatement("$T $L = $L($L)", RETRIEVER_CLASS, RETRIEVER_VAR, PARSE_METHOD, BUNDLE_VAR)
                        .beginControlFlow("if($L.$L())", RETRIEVER_VAR, IS_NULL_METHOD)
                        .addStatement("$L.$L($L)", RETRIEVER_VAR, INTO_METHOD, BUNDLER_VAR)
                        .endControlFlow()
                        .build()
        );
        return methods;
    }}
