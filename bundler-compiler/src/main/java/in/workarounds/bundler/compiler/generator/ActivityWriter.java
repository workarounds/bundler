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
 * Created by madki on 24/10/15.
 */
public class ActivityWriter extends Writer {
    protected static final String ACTIVITY_VAR = "activity";

    protected ActivityWriter(Provider provider, ReqBundlerModel reqBundlerModel, List<ArgModel> cargoList, List<StateModel> states, String packageName) {
        super(provider, reqBundlerModel, cargoList, states, packageName);
    }

    @Override
    protected List<MethodSpec> getAdditionalHelperMethods() {
        List<MethodSpec> methods = super.getAdditionalHelperMethods();
        methods.add(
                MethodSpec.methodBuilder(model.methods().parse())
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(CommonClasses.INTENT, model.vars().intent())
                        .returns(model.classes().parser())
                        .beginControlFlow("if($L == null)", model.vars().intent())
                        .addStatement("return new $T(null)", model.classes().parser())
                        .endControlFlow()
                        .addStatement("return $L($L.getExtras())", model.methods().parse(), model.vars().intent())
                        .build()
        );
        methods.add(
                MethodSpec.methodBuilder(model.methods().inject())
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(model.getClassName(), ACTIVITY_VAR)
                        .addStatement("$T $L = $L($L.getIntent())", model.classes().parser(), model.vars().parser(), model.methods().parse(), ACTIVITY_VAR)
                        .beginControlFlow("if(!$L.$L())", model.vars().parser(), model.methods().isNull())
                        .addStatement("$L.$L($L)", model.vars().parser(), model.methods().into(), ACTIVITY_VAR)
                        .endControlFlow()
                        .build()
        );
        return methods;
    }

    @Override
    protected List<MethodSpec> getAdditionalSupplierMethods() {
        List<MethodSpec> methods = super.getAdditionalSupplierMethods();
        methods.add(
                MethodSpec.methodBuilder(model.methods().intent())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(CommonClasses.CONTEXT, model.vars().context())
                        .returns(CommonClasses.INTENT)
                        .addStatement("$T $L = new $T($L, $T.class)", CommonClasses.INTENT, model.vars().intent(), CommonClasses.INTENT, model.vars().context(), model.getClassName())
                        .addStatement("$L.putExtras($L())", model.vars().intent(), model.methods().bundle())
                        .addStatement("return $L", model.vars().intent())
                        .build()
        );
        methods.add(
                MethodSpec.methodBuilder(model.methods().start())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(CommonClasses.CONTEXT, model.vars().context())
                        .addStatement("$L.startActivity($L($L))", model.vars().context(), model.methods().intent(), model.vars().context())
                        .build()
        );
        return methods;
    }
}
