package in.workarounds.autorickshaw.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.autorickshaw.compiler.Provider;
import in.workarounds.autorickshaw.compiler.model.DestinationModel;
import in.workarounds.autorickshaw.compiler.model.PassengerModel;
import in.workarounds.autorickshaw.compiler.support.TypeMatcher;
import in.workarounds.autorickshaw.compiler.support.helper.SupportHelper;
import in.workarounds.autorickshaw.compiler.util.CommonClasses;
import in.workarounds.autorickshaw.compiler.util.StringUtils;

/**
 * Created by madki on 19/10/15.
 */
public class TestWriter {
    private Provider provider;
    private DestinationModel destinationModel;
    private List<SupportHelper> helpers;
    private static final String LOADER_PREFIX = "Load";
    private static final String UN_LOADER_PREFIX = "UnLoad";
    private static final String KEYS_PREFIX = "IntentKeys";
    private static final String BUILDER_NAME = "Builder";
    private static final String PARSER_NAME = "Parser";

    private String LOADER_SIMPLE_NAME;
    private String UN_LOADER_SIMPLE_NAME;
    private ClassName BUILDER_CLASS;
    private ClassName PARSER_CLASS;

    private String KEYS_SIMPLE_NAME;
    private ClassName KEYS_CLASS;

    private String CONTEXT_VAR = "context";
    private String BUNDLE_VAR = "bundle";

    public TestWriter(Provider provider, DestinationModel destinationModel, List<PassengerModel> passengerModels) {
        this.provider = provider;
        this.destinationModel = destinationModel;

        helpers = new ArrayList<>();

        for (PassengerModel passengerModel : passengerModels) {
            helpers.add(TypeMatcher.getSupportHelper(passengerModel));
        }

        LOADER_SIMPLE_NAME = LOADER_PREFIX + destinationModel.getSimpleName();
        UN_LOADER_SIMPLE_NAME = UN_LOADER_PREFIX + destinationModel.getSimpleName();

        String LOADER_NAME = destinationModel.getPackageName() + "." + LOADER_SIMPLE_NAME;
        BUILDER_CLASS = ClassName.bestGuess(LOADER_NAME + "." + BUILDER_NAME);

        String UNLOADER_NAME = destinationModel.getPackageName() + "." + UN_LOADER_SIMPLE_NAME;
        PARSER_CLASS = ClassName.bestGuess(UNLOADER_NAME + "." + PARSER_NAME);

        KEYS_SIMPLE_NAME = KEYS_PREFIX + destinationModel.getSimpleName();
        KEYS_CLASS = ClassName.get(destinationModel.getPackageName(), KEYS_SIMPLE_NAME);
    }

    public JavaFile brewKeys() {
        TypeSpec.Builder keyBuilder = TypeSpec.classBuilder(KEYS_SIMPLE_NAME);
        for (SupportHelper helper : helpers) {
            FieldSpec field = FieldSpec.builder(String.class, helper.getIntentKey(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", helper.getIntentKey().toLowerCase())
                    .build();
            keyBuilder.addField(field);
        }
        return JavaFile.builder(destinationModel.getPackageName(), keyBuilder.build()).build();
    }

    public JavaFile brewLoader() {
        MethodSpec from = MethodSpec.methodBuilder("from")
                .addParameter(CommonClasses.CONTEXT, CONTEXT_VAR)
                .returns(BUILDER_CLASS)
                .addStatement("return new $T($L)", BUILDER_CLASS, CONTEXT_VAR)
                .build();
        TypeSpec loader = TypeSpec.classBuilder(LOADER_SIMPLE_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(from)
                .addType(brewBuilder())
                .build();
        return JavaFile.builder(destinationModel.getPackageName(), loader).build();
    }

    private TypeSpec brewBuilder() {
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

        for (SupportHelper helper : helpers) {
            builder.addField(helper.getBuilderField())
                    .addMethods(helper.getBuilderMethods(BUILDER_CLASS));
            bundleBuilder.beginControlFlow("if($L != null)", helper.getLabel());
            helper.addToBundle(bundleBuilder, BUNDLE_VAR, KEYS_CLASS);
            bundleBuilder.endControlFlow();
        }

        bundleBuilder.addStatement("return $L", BUNDLE_VAR);
        builder.addMethod(bundleBuilder.build());

        return builder.build();
    }

    public JavaFile brewUnLoader() {
        MethodSpec from = MethodSpec.methodBuilder("from")
                .addModifiers(Modifier.PUBLIC)
                .returns(PARSER_CLASS)
                .addParameter(ParameterSpec.builder(CommonClasses.BUNDLE, BUNDLE_VAR).build())
                .addStatement("return new $T($L)", PARSER_CLASS, BUNDLE_VAR)
                .build();

        TypeSpec unLoader = TypeSpec.classBuilder(UN_LOADER_SIMPLE_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(from)
                .addType(brewParser())
                .build();

        return JavaFile.builder(destinationModel.getPackageName(), unLoader).build();
    }

    private TypeSpec brewParser() {
        String DESTINATION_VAR = StringUtils.getVariableName(destinationModel.getSimpleName());
        String HAS_PREFIX = "has";

        FieldSpec bundle = FieldSpec.builder(CommonClasses.BUNDLE, BUNDLE_VAR, Modifier.PRIVATE)
                .build();
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterSpec.builder(CommonClasses.BUNDLE, BUNDLE_VAR).build())
                .addStatement("this.$L = $L", BUNDLE_VAR, BUNDLE_VAR)
                .build();

        MethodSpec.Builder intoBuilder = MethodSpec.methodBuilder("into")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(destinationModel.getClassName(), DESTINATION_VAR);

        TypeSpec.Builder builder = TypeSpec.classBuilder(PARSER_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addField(bundle)
                .addMethod(constructor);

        for (SupportHelper helper : helpers) {
            String hasMethodName = HAS_PREFIX + StringUtils.getClassName(helper.getLabel());
            MethodSpec hasMethod = MethodSpec.methodBuilder(hasMethodName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(boolean.class)
                    .addStatement("return $L.containsKey($T.$L)", BUNDLE_VAR, KEYS_CLASS, helper.getIntentKey())
                    .build();
            builder.addMethod(hasMethod);
            builder.addMethods(helper.getParserMethods(hasMethodName, BUNDLE_VAR, KEYS_CLASS));

            intoBuilder.beginControlFlow("if($L())", hasMethodName);
            helper.addIntoStatement(intoBuilder, DESTINATION_VAR);
            intoBuilder.endControlFlow();
        }

        builder.addMethod(intoBuilder.build());
        return builder.build();
    }
}
