package in.workarounds.bundler.compiler.generator;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.model.ArgModel;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.model.StateModel;

/**
 * Created by madki on 24/10/15.
 */
public class FragmentWriter extends Writer {
    protected static final String FRAGMENT_VAR = "fragment";

    protected FragmentWriter(Provider provider, ReqBundlerModel reqBundlerModel, List<ArgModel> cargoList, List<StateModel> states) {
        super(provider, reqBundlerModel, cargoList, states);
    }

        @Override
    protected List<MethodSpec> getAdditionalHelperMethods() {
        List<MethodSpec> methods = super.getAdditionalHelperMethods();
        methods.add(
                MethodSpec.methodBuilder(INJECT_METHOD)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(reqBundlerModel.getClassName(), FRAGMENT_VAR)
                        .addStatement("$T $L = $L($L.getArguments())", RETRIEVER_CLASS, RETRIEVER_VAR, RETRIEVE_METHOD, FRAGMENT_VAR)
                        .beginControlFlow("if($L.$L())", RETRIEVER_VAR, IS_NULL_METHOD)
                        .addStatement("$L.$L($L)", RETRIEVER_VAR, INTO_METHOD, FRAGMENT_VAR)
                        .endControlFlow()
                        .build()
        );
        return methods;
    }

    @Override
    protected List<MethodSpec> getAdditionalSupplierMethods() {
        List<MethodSpec> methods = super.getAdditionalSupplierMethods();
        methods.add(
                MethodSpec.methodBuilder(CREATE_METHOD)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(reqBundlerModel.getClassName())
                        .addStatement("$T $L = new $T()", reqBundlerModel.getClassName(), FRAGMENT_VAR, reqBundlerModel.getClassName())
                        .addStatement("$L.setArguments($L())", FRAGMENT_VAR, BUNDLE_VAR)
                        .addStatement("return $L", FRAGMENT_VAR)
                        .build()
        );
        return methods;
    }
}
