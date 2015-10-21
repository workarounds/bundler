package in.workarounds.autorickshaw.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import in.workarounds.autorickshaw.compiler.Provider;
import in.workarounds.autorickshaw.compiler.model.CargoModel;
import in.workarounds.autorickshaw.compiler.model.DestinationModel;
import in.workarounds.autorickshaw.compiler.support.SupportResolver;
import in.workarounds.autorickshaw.compiler.support.helper.TypeHelper;
import in.workarounds.autorickshaw.compiler.util.CommonClasses;
import in.workarounds.autorickshaw.compiler.util.StringUtils;

/**
 * Created by madki on 19/10/15.
 */
public class BaseWriter {
    private Provider provider;
    private DestinationModel destinationModel;
    private List<CargoModel> cargoList;
    private List<TypeHelper> typeHelpers;
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
    private String DEFAULT_VAR = "defaultValue";

    public BaseWriter(Provider provider, DestinationModel destinationModel, List<CargoModel> cargoList) {
        this.provider = provider;
        this.destinationModel = destinationModel;
        this.cargoList = cargoList;
        this.typeHelpers = new ArrayList<>();

        TypeHelper helper;
        for (CargoModel cargo : cargoList) {
            helper = SupportResolver.getHelper(cargo, provider.elementUtils());
            if(helper != null) {
                typeHelpers.add(helper);
            } else {
                throw new IllegalStateException(String.format("No helper found for %s %s", cargo.getTypeName(), cargo.getLabel()));
            }
        }

        LOADER_SIMPLE_NAME = LOADER_PREFIX + destinationModel.getSimpleName();
        UN_LOADER_SIMPLE_NAME = UN_LOADER_PREFIX + destinationModel.getSimpleName();

        String LOADER_NAME = destinationModel.getPackageName() + "." + LOADER_SIMPLE_NAME;
        BUILDER_CLASS = ClassName.bestGuess(LOADER_NAME + "." + BUILDER_NAME);

        String UN_LOADER_NAME = destinationModel.getPackageName() + "." + UN_LOADER_SIMPLE_NAME;
        PARSER_CLASS = ClassName.bestGuess(UN_LOADER_NAME + "." + PARSER_NAME);

        KEYS_SIMPLE_NAME = KEYS_PREFIX + destinationModel.getSimpleName();
        KEYS_CLASS = ClassName.get(destinationModel.getPackageName(), KEYS_SIMPLE_NAME);
    }

    public JavaFile brewKeys() {
        TypeSpec.Builder keyBuilder = TypeSpec.classBuilder(KEYS_SIMPLE_NAME);
        for (TypeHelper helper : typeHelpers) {
            FieldSpec field = FieldSpec.builder(String.class, helper.getIntentKey(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", helper.getIntentKey().toLowerCase())
                    .build();
            keyBuilder.addField(field);
        }
        return JavaFile.builder(destinationModel.getPackageName(), keyBuilder.build()).build();
    }

    public JavaFile brewLoader() {
        MethodSpec from = MethodSpec.methodBuilder("from")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
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
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addField(context)
                .addMethod(constructor);

        TypeName type;
        String label;
        for (int i = 0; i < cargoList.size(); i++) {
            type = cargoList.get(i).getTypeName();
            label = cargoList.get(i).getLabel();

            if (type.isPrimitive()) {
                builder.addField(type.box(), label, Modifier.PRIVATE);
            } else {
                builder.addField(type, label, Modifier.PRIVATE);
            }

            bundleBuilder.beginControlFlow("if($L != null)", label);
            bundleBuilder.addStatement("$L.put$L($T.$L, $L)",
                    BUNDLE_VAR,
                    typeHelpers.get(i).getBundleMethodSuffix(),
                    KEYS_CLASS,
                    typeHelpers.get(i).getIntentKey(),
                    label);
            bundleBuilder.endControlFlow();

            builder.addMethod(getBuilderSetter(type, label));
        }

        bundleBuilder.addStatement("return $L", BUNDLE_VAR);
        builder.addMethod(bundleBuilder.build());

        return builder.build();
    }

    private MethodSpec getBuilderSetter(TypeName type, String label) {
        return MethodSpec.methodBuilder(label)
                .addModifiers(Modifier.PUBLIC)
                .returns(BUILDER_CLASS)
                .addParameter(type, label)
                .addStatement("this.$L = $L", label, label)
                .addStatement("return this")
                .build();
    }

    public JavaFile brewUnLoader() {
        MethodSpec from = MethodSpec.methodBuilder("from")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
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
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addField(bundle)
                .addMethod(constructor);

        String label;
        TypeName type;
        String hasMethod;
        for (int i = 0; i < cargoList.size(); i++) {
            label = cargoList.get(i).getLabel();
            type = cargoList.get(i).getTypeName();

            hasMethod = HAS_PREFIX + StringUtils.getClassName(label);
            builder.addMethod(getParserHasMethod(hasMethod, typeHelpers.get(i).getIntentKey()));
            builder.addMethod(getParserGetterMethod(type, label, hasMethod, typeHelpers.get(i)));

            intoBuilder.beginControlFlow("if($L())", hasMethod);
            if (type.isPrimitive()) {
                intoBuilder.addStatement("$L.$L = $L($L.$L)", DESTINATION_VAR, label,
                        label, DESTINATION_VAR, label);
            } else {
                intoBuilder.addStatement("$L.$L = $L()", DESTINATION_VAR, label, label);
            }
            intoBuilder.endControlFlow();
            // TODO throw exception in else block if @NotEmpty present
        }

        builder.addMethod(intoBuilder.build());
        return builder.build();
    }

    private MethodSpec getParserHasMethod(String hasMethod, String intentKey) {
        return MethodSpec.methodBuilder(hasMethod)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return $L.containsKey($T.$L)", BUNDLE_VAR, KEYS_CLASS, intentKey)
                .build();
    }

    private MethodSpec getParserGetterMethod(TypeName type, String label, String hasMethod, TypeHelper helper) {
        MethodSpec.Builder getterMethodBuilder = MethodSpec.methodBuilder(label)
                .addModifiers(Modifier.PUBLIC)
                .returns(type);

        if (type.isPrimitive()) {
            getterMethodBuilder.addParameter(type, DEFAULT_VAR);
        }

        if (type.isPrimitive()) {
            getterMethodBuilder.addStatement("return $L.get$L($T.$L, $L)",
                    BUNDLE_VAR,
                    helper.getBundleMethodSuffix(),
                    KEYS_CLASS,
                    helper.getIntentKey(),
                    DEFAULT_VAR
            );
        } else if(helper.requiresCasting()) {
            getterMethodBuilder.beginControlFlow("if($L())", hasMethod);
            getterMethodBuilder.addStatement("return ($T) $L.get$L($T.$L)",
                    type,
                    BUNDLE_VAR,
                    helper.getBundleMethodSuffix(),
                    KEYS_CLASS,
                    helper.getIntentKey()
            );
            getterMethodBuilder.endControlFlow();
            getterMethodBuilder.addStatement("return null");
        } else {
            getterMethodBuilder.addStatement("return $L.get$L($T.$L)",
                    BUNDLE_VAR,
                    helper.getBundleMethodSuffix(),
                    KEYS_CLASS,
                    helper.getIntentKey()
            );
        }


        return getterMethodBuilder.build();
    }
}
