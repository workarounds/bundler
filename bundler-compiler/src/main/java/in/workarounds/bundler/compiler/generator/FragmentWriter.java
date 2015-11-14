package in.workarounds.bundler.compiler.generator;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.util.names.ClassProvider;
import in.workarounds.bundler.compiler.util.names.MethodName;
import in.workarounds.bundler.compiler.util.names.VarName;

/**
 * Created by madki on 24/10/15.
 */
public class FragmentWriter extends Writer {

    protected FragmentWriter(Provider provider, ReqBundlerModel reqBundlerModel) {
        super(provider, reqBundlerModel);
    }

    @Override
    protected List<MethodSpec> getAdditionalBundlerMethods() {
        List<MethodSpec> methods = super.getAdditionalBundlerMethods();
        methods.add(
                MethodSpec.methodBuilder(MethodName.inject)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(model.getClassName(), VarName.from(model))
                        .addStatement("$T $L = $T.$L($L.getArguments())", ClassProvider.parser(model), VarName.parser, ClassProvider.helper(model), MethodName.parse, VarName.from(model))
                        .beginControlFlow("if($L.$L())", VarName.parser, MethodName.isNull)
                        .addStatement("$L.$L($L)", VarName.parser, MethodName.into, VarName.from(model))
                        .endControlFlow()
                        .build()
        );
        return methods;
    }

    @Override
    protected List<MethodSpec> getAdditionalSupplierMethods() {
        List<MethodSpec> methods = super.getAdditionalSupplierMethods();
        methods.add(
                MethodSpec.methodBuilder(MethodName.create)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(model.getClassName())
                        .addStatement("$T $L = new $T()", model.getClassName(), VarName.from(model), model.getClassName())
                        .addStatement("$L.setArguments($L())", VarName.from(model), VarName.bundle)
                        .addStatement("return $L", VarName.from(model))
                        .build()
        );
        return methods;
    }
}
