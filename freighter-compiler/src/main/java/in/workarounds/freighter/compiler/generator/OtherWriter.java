package in.workarounds.freighter.compiler.generator;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.freighter.compiler.Provider;
import in.workarounds.freighter.compiler.model.CargoModel;
import in.workarounds.freighter.compiler.model.FreighterModel;
import in.workarounds.freighter.compiler.util.CommonClasses;

/**
 * Created by madki on 25/10/15.
 */
public class OtherWriter extends Writer {
    protected static final String FREIGHTER_VAR = "freighter";

    protected OtherWriter(Provider provider, FreighterModel freighterModel, List<CargoModel> cargoList) {
        super(provider, freighterModel, cargoList);
    }


@Override
    protected List<MethodSpec> getAdditionalHelperMethods() {
        List<MethodSpec> methods = super.getAdditionalHelperMethods();
        methods.add(
                MethodSpec.methodBuilder(INJECT_METHOD)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(freighterModel.getClassName(), FREIGHTER_VAR)
                        .addParameter(CommonClasses.BUNDLE, BUNDLE_VAR)
                        .beginControlFlow("if($L != null)", BUNDLE_VAR)
                        .addStatement("$L($L).$L($L)", RETRIEVE_METHOD, BUNDLE_VAR, INTO_METHOD, FREIGHTER_VAR)
                        .endControlFlow()
                        .build()
        );
        return methods;
    }}
