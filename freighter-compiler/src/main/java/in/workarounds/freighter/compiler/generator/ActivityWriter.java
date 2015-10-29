package in.workarounds.freighter.compiler.generator;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.freighter.compiler.Provider;
import in.workarounds.freighter.compiler.model.AnnotatedField;
import in.workarounds.freighter.compiler.model.FreighterModel;
import in.workarounds.freighter.compiler.util.CommonClasses;

/**
 * Created by madki on 24/10/15.
 */
public class ActivityWriter extends Writer {
    protected static final String ACTIVITY_VAR = "activity";

    protected ActivityWriter(Provider provider, FreighterModel freighterModel, List<AnnotatedField> cargoList) {
        super(provider, freighterModel, cargoList);
    }

    @Override
    protected List<MethodSpec> getAdditionalHelperMethods() {
        List<MethodSpec> methods = super.getAdditionalHelperMethods();
        methods.add(
                MethodSpec.methodBuilder(RETRIEVE_METHOD)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(CommonClasses.INTENT, INTENT_VAR)
                        .returns(RETRIEVER_CLASS)
                        .beginControlFlow("if($L != null)", INTENT_VAR)
                        .addStatement("return $L($L.getExtras())", RETRIEVE_METHOD, INTENT_VAR)
                        .endControlFlow()
                        .beginControlFlow("else")
                        .addStatement("return null")
                        .endControlFlow()
                        .build()
        );
        methods.add(
                MethodSpec.methodBuilder(INJECT_METHOD)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(freighterModel.getClassName(), ACTIVITY_VAR)
                        .addStatement("$T $L = $L($L.getIntent())", RETRIEVER_CLASS, RETRIEVER_VAR, RETRIEVE_METHOD, ACTIVITY_VAR)
                        .beginControlFlow("if($L != null)", RETRIEVER_VAR)
                        .addStatement("$L.$L($L)", RETRIEVER_VAR, INTO_METHOD, ACTIVITY_VAR)
                        .endControlFlow()
                        .build()
        );
        return methods;
    }

    @Override
    protected List<MethodSpec> getAdditionalSupplierMethods() {
        List<MethodSpec> methods = super.getAdditionalSupplierMethods();
        methods.add(
                MethodSpec.methodBuilder(INTENT_METHOD)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(CommonClasses.CONTEXT, CONTEXT_VAR)
                        .returns(CommonClasses.INTENT)
                        .addStatement("$T $L = new $T($L, $T.class)", CommonClasses.INTENT, INTENT_VAR, CommonClasses.INTENT, CONTEXT_VAR, freighterModel.getClassName())
                        .addStatement("$L.putExtras($L())", INTENT_VAR, BUNDLE_METHOD)
                        .addStatement("return $L", INTENT_VAR)
                        .build()
        );
        methods.add(
                MethodSpec.methodBuilder(START_METHOD)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(CommonClasses.CONTEXT, CONTEXT_VAR)
                        .addStatement("$L.startActivity($L($L))", CONTEXT_VAR, INTENT_METHOD, CONTEXT_VAR)
                        .build()
        );
        return methods;
    }
}
