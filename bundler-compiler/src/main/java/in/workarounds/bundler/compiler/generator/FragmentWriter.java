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

    protected FragmentWriter(Provider provider, ReqBundlerModel reqBundlerModel, List<ArgModel> cargoList, List<StateModel> states, String packageName) {
        super(provider, reqBundlerModel, cargoList, states, packageName);
    }

    @Override
    protected List<MethodSpec> getAdditionalBundlerMethods() {
        List<MethodSpec> methods = super.getAdditionalBundlerMethods();
        methods.add(
                MethodSpec.methodBuilder(model.methods().inject())
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(model.getClassName(), FRAGMENT_VAR)
                        .addStatement("$T $L = $T.$L($L.getArguments())", model.classes().parser(), model.vars().parser(), model.classes().helper(), model.methods().parse(), FRAGMENT_VAR)
                        .beginControlFlow("if($L.$L())", model.vars().parser(), model.methods().isNull())
                        .addStatement("$L.$L($L)", model.vars().parser(), model.methods().into(), FRAGMENT_VAR)
                        .endControlFlow()
                        .build()
        );
        return methods;
    }

    @Override
    protected List<MethodSpec> getAdditionalSupplierMethods() {
        List<MethodSpec> methods = super.getAdditionalSupplierMethods();
        methods.add(
                MethodSpec.methodBuilder(model.methods().create())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(model.getClassName())
                        .addStatement("$T $L = new $T()", model.getClassName(), FRAGMENT_VAR, model.getClassName())
                        .addStatement("$L.setArguments($L())", FRAGMENT_VAR, model.vars().bundle())
                        .addStatement("return $L", FRAGMENT_VAR)
                        .build()
        );
        return methods;
    }
}
