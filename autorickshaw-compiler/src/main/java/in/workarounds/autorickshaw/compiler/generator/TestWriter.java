package in.workarounds.autorickshaw.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.autorickshaw.compiler.Provider;
import in.workarounds.autorickshaw.compiler.model.DestinationModel;
import in.workarounds.autorickshaw.compiler.model.PassengerModel;
import in.workarounds.autorickshaw.compiler.support.TypeMatcher;
import in.workarounds.autorickshaw.compiler.support.helper.SupportHelper;
import in.workarounds.autorickshaw.compiler.util.CommonClasses;

/**
 * Created by madki on 19/10/15.
 */
public class TestWriter {
    private Provider provider;
    private DestinationModel destinationModel;
    private List<PassengerModel> passengerModels;
    private static final String MAKER_PREFIX = "Intent";
    private static final String KEYS_PREFIX = "Keys";

    private String MAKER_NAME;
    private String MAKER_SIMPLE_NAME;
    private String BUILDER_NAME = "Builder";
    private ClassName BUILDER_CLASS;

    private String KEYS_SIMPLE_NAME;
    private ClassName KEYS_CLASS;

    private String CONTEXT_VAR = "context";

    public TestWriter(Provider provider, DestinationModel destinationModel, List<PassengerModel> passengerModels) {
        this.provider = provider;
        this.destinationModel = destinationModel;
        this.passengerModels = passengerModels;

        MAKER_SIMPLE_NAME = MAKER_PREFIX + destinationModel.getSimpleName();
        MAKER_NAME = destinationModel.getPackageName() + "." + MAKER_SIMPLE_NAME;
        BUILDER_CLASS = ClassName.bestGuess(MAKER_NAME + "." + BUILDER_NAME);

        KEYS_SIMPLE_NAME = KEYS_PREFIX + destinationModel.getSimpleName();
        KEYS_CLASS = ClassName.get(destinationModel.getPackageName(), KEYS_SIMPLE_NAME);
    }

    public JavaFile brewKeys() {
        TypeSpec.Builder keyBuilder = TypeSpec.classBuilder(KEYS_SIMPLE_NAME);
        for (PassengerModel passenger : passengerModels) {
            SupportHelper helper = TypeMatcher.getSupportHelper(passenger);
            FieldSpec field = FieldSpec.builder(String.class, helper.getIntentKey(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", helper.getIntentKey().toLowerCase())
                    .build();
            keyBuilder.addField(field);
        }
        return JavaFile.builder(destinationModel.getPackageName(), keyBuilder.build()).build();
    }

    public JavaFile brewMaker() {
        MethodSpec from = MethodSpec.methodBuilder("from")
                .addParameter(CommonClasses.CONTEXT, CONTEXT_VAR)
                .returns(BUILDER_CLASS)
                .addStatement("return new $T($L)", BUILDER_CLASS, CONTEXT_VAR)
                .build();
        TypeSpec maker = TypeSpec.classBuilder(MAKER_SIMPLE_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(from)
                .addType(brewBuilder())
                .build();
        return JavaFile.builder(destinationModel.getPackageName(), maker).build();
    }

    private TypeSpec brewBuilder() {
        String BUNDLE_VAR = "bundle";

        FieldSpec context = FieldSpec.builder(CommonClasses.CONTEXT, CONTEXT_VAR, Modifier.PRIVATE)
                .build();
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(CommonClasses.CONTEXT, CONTEXT_VAR)
                .addStatement("this.$L = $L", CONTEXT_VAR, CONTEXT_VAR)
                .build();

        MethodSpec.Builder bundleBuilder = MethodSpec.methodBuilder("bundle")
                .addModifiers(Modifier.PUBLIC)
                .returns(CommonClasses.BUNDLE)
                .addStatement("$T $L = new $T()",
                        CommonClasses.BUNDLE,
                        BUNDLE_VAR,
                        CommonClasses.BUNDLE);

        TypeSpec.Builder builder = TypeSpec.classBuilder(BUILDER_NAME)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addField(context)
                .addMethod(constructor);

        for (PassengerModel passenger : passengerModels) {
            SupportHelper helper = TypeMatcher.getSupportHelper(passenger);
            builder.addField(helper.getBuilderField())
                    .addMethods(helper.getBuilderMethods(BUILDER_CLASS));
            bundleBuilder.beginControlFlow("if($L != null)", passenger.getLabel());
            helper.addToBundle(bundleBuilder, BUNDLE_VAR, KEYS_CLASS);
            bundleBuilder.endControlFlow();
        }

        bundleBuilder.addStatement("return $L", BUNDLE_VAR);
        builder.addMethod(bundleBuilder.build());

        return builder.build();
    }
}
