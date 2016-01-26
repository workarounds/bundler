package in.workarounds.bundler.compiler.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

import in.workarounds.bundler.compiler.model.ArgModel;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.util.names.ClassProvider;

/**
 * Created by madki on 17/11/15.
 */
public class KeysGenerator {
    private ReqBundlerModel model;

    public KeysGenerator(ReqBundlerModel model) {
        this.model = model;
    }

    public TypeSpec createKeysInterface() {
        TypeSpec.Builder keyBuilder = TypeSpec.interfaceBuilder(ClassProvider.keys(model).simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        for (ArgModel arg : model.getArgs()) {
            FieldSpec fieldSpec = FieldSpec.builder(String.class, arg.getKeyConstant(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", arg.getKeyValue())
                    .build();
            keyBuilder.addField(fieldSpec);
        }
        return keyBuilder.build();
    }
}
