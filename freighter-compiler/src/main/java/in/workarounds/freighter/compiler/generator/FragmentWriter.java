package in.workarounds.freighter.compiler.generator;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.freighter.compiler.Provider;
import in.workarounds.freighter.compiler.model.AnnotatedField;
import in.workarounds.freighter.compiler.model.FreighterModel;

/**
 * Created by madki on 24/10/15.
 */
public class FragmentWriter extends Writer {
    protected static final String FRAGMENT_VAR = "fragment";

    protected FragmentWriter(Provider provider, FreighterModel freighterModel, List<AnnotatedField> cargoList) {
        super(provider, freighterModel, cargoList);
    }

        @Override
    protected List<MethodSpec> getAdditionalHelperMethods() {
        List<MethodSpec> methods = super.getAdditionalHelperMethods();
        methods.add(
                MethodSpec.methodBuilder(INJECT_METHOD)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(freighterModel.getClassName(), FRAGMENT_VAR)
                        .addStatement("$T $L = $L($L.getArguments())", RETRIEVER_CLASS, RETRIEVER_VAR, RETRIEVE_METHOD, FRAGMENT_VAR)
                        .beginControlFlow("if($L != null)", RETRIEVER_VAR)
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
                        .returns(freighterModel.getClassName())
                        .addStatement("$T $L = new $T()", freighterModel.getClassName(), FRAGMENT_VAR, freighterModel.getClassName())
                        .addStatement("$L.setArguments($L())", FRAGMENT_VAR, BUNDLE_VAR)
                        .addStatement("return $L", FRAGMENT_VAR)
                        .build()
        );
        return methods;
    }
}
