package in.workarounds.bundler.compiler.support;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.model.ArgModel;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.util.StringUtils;

/**
 * Created by madki on 12/11/15.
 */
public class MethodAggregator {
    private List<String> methods;
    private Provider provider;

    public MethodAggregator(Provider provider) {
        methods = new ArrayList<>();
        this.provider = provider;
    }

    public MethodSpec getBundlerBuildMethod(ReqBundlerModel model, List<ArgModel> argModels) {
            String bundlerMethodName = model.getBundlerMethodName().isEmpty()?
                    StringUtils.getVariableName(model.getSimpleName()) : model.getBundlerMethodName();
            boolean requireAll = model.requireAll();
            List<ArgModel> methodArgs = new ArrayList<>();

            for (ArgModel arg: argModels) {
                if(arg.isRequired(requireAll)) {
                    methodArgs.add(arg);
                }
            }

        checkMethodsValidity(bundlerMethodName, model.getElement());

        return bundlerBuildMethod(model, bundlerMethodName, methodArgs);
    }

    private void checkMethodsValidity(String bundlerMethodName, Element element) {
            if(methods.contains(bundlerMethodName)) {
                provider.error(element, "MethodName already used, please change the method name: %s", bundlerMethodName);
                provider.reportError();
            } else {
                methods.add(bundlerMethodName);
            }
    }

    protected MethodSpec bundlerBuildMethod(ReqBundlerModel model, String methodName, List<ArgModel> args) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(model.classes().builder());

        for (ArgModel arg : args) {
            builder.addParameter(getArgParameter(arg));
        }

        String statement = "return $T.$L()";
        for (ArgModel arg : args) {
            statement = statement + String.format(".%s(%s)", arg.getLabel(), arg.getLabel());
        }

        builder.addStatement(statement, model.classes().helper(), model.methods().build());
        return builder.build();
    }

    private ParameterSpec getArgParameter(ArgModel arg) {
        return ParameterSpec.builder(
                arg.getTypeName(),
                arg.getLabel()
        ).addAnnotations(arg.getSupportAnnotations())
                .build();
    }


}
