package in.workarounds.bundler.compiler.support;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public HashMap<String, List<ArgModel>> getMethodMap(ReqBundlerModel model, List<ArgModel> argModels) {
        HashMap<String, List<ArgModel>> methodMap = new HashMap<>();
        for (ArgModel arg : argModels) {
            for (String s : arg.getMethods()) {
                if (!s.isEmpty()) {
                    addToMap(methodMap, s, arg);
                }
            }
        }

        if(methodMap.isEmpty()) {
            String bundlerMethodName = model.getBundlerMethodName().isEmpty()?
                    StringUtils.getVariableName(model.getSimpleName()) : model.getBundlerMethodName();
            boolean requireAll = model.requireAll();
            List<ArgModel> methodArgs = new ArrayList<>();

            for (ArgModel arg: argModels) {
                if(arg.isRequired(requireAll)) {
                    methodArgs.add(arg);
                }
            }
            methodMap.put(bundlerMethodName, methodArgs);
        }

        checkMethodsValidity(methodMap.keySet(), model.getElement());

        return methodMap;
    }

    private void checkMethodsValidity(Set<String> currentMethods, Element element) {
        for (String method: currentMethods) {
            if(methods.contains(method)) {
                provider.error(element, "MethodName already used, please change the method name: %s", method);
                provider.reportError();
            } else {
                methods.add(method);
            }
        }
    }

    public List<MethodSpec> getBundlerBuildMethods(ReqBundlerModel model, HashMap<String, List<ArgModel>> methodMap) {
        List<MethodSpec> methods = new ArrayList<>();
        for(Map.Entry<String, List<ArgModel>> entry: methodMap.entrySet()) {
            methods.add(bundlerBuildMethod(model, entry.getKey(), entry.getValue()));
        }
        return methods;
    }

    private void addToMap(HashMap<String, List<ArgModel>> methodMap, String method, ArgModel arg) {
        if(methodMap.containsKey(method)) {
            methodMap.get(method).add(arg);
        } else {
            List<ArgModel> args = new ArrayList<>();
            args.add(arg);
            methodMap.put(method, args);
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
