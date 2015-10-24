package in.workarounds.freighter.compiler.generator;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.freighter.compiler.Provider;
import in.workarounds.freighter.compiler.model.CargoModel;
import in.workarounds.freighter.compiler.model.FreighterModel;
import in.workarounds.freighter.compiler.util.CommonClasses;

/**
 * Created by madki on 24/10/15.
 */
public class ActivityWriter extends Writer {
    protected static final String ACTIVITY_VAR = "activity";

    protected ActivityWriter(Provider provider, FreighterModel freighterModel, List<CargoModel> cargoList) {
        super(provider, freighterModel, cargoList);
    }

    @Override
    protected List<MethodSpec> getAdditionalHelperMethods() {
        List<MethodSpec> methods = super.getAdditionalHelperMethods();
        methods.add(
                MethodSpec.methodBuilder(UNLOAD_METHOD)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(CommonClasses.INTENT, INTENT_VAR)
                        .returns(UN_LOADER_CLASS)
                        .addStatement("return $L($L.getExtras())", UNLOAD_METHOD, INTENT_VAR)
                        .build()
        );
        methods.add(
                MethodSpec.methodBuilder(UNLOAD_METHOD)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(freighterModel.getClassName(), ACTIVITY_VAR)
                        .addStatement("$L($L.getIntent()).$L($L)", UNLOAD_METHOD, ACTIVITY_VAR, INTO_METHOD, ACTIVITY_VAR)
                        .build()
        );
        return methods;
    }

    @Override
    protected List<MethodSpec> getAdditionalLoaderMethods() {
        List<MethodSpec> methods = super.getAdditionalLoaderMethods();
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
