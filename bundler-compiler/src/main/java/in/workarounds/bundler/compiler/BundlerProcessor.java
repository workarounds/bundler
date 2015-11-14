package in.workarounds.bundler.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.State;
import in.workarounds.bundler.compiler.generator.Writer;
import in.workarounds.bundler.compiler.model.ArgModel;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.model.StateModel;
import in.workarounds.bundler.compiler.support.MethodAggregator;

@AutoService(Processor.class)
public class BundlerProcessor extends AbstractProcessor implements Provider {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    private boolean errorOccurred;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();

    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        String packageName = "in.workarounds.bundler";
        MethodAggregator methodAggregator = new MethodAggregator(this);

        List<ReqBundlerModel> reqBundlerModels = new ArrayList<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(RequireBundler.class)) {
            ReqBundlerModel model = new ReqBundlerModel(element, this);
            if (hasErrorOccurred()) return true;
            reqBundlerModels.add(model);
        }

        if (reqBundlerModels.size() == 0) return true;

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(bundlerClass().simpleName())
                .addModifiers(Modifier.PUBLIC);

        for (ReqBundlerModel model : reqBundlerModels) {
            List<ArgModel> argList = new ArrayList<>();
            for (Element possibleCargo : model.getElement().getEnclosedElements()) {
                Arg arg = possibleCargo.getAnnotation(Arg.class);
                if (arg != null) {
                    ArgModel argModel = new ArgModel(possibleCargo, this);
                    argList.add(argModel);
                }
            }

            List<StateModel> states = new ArrayList<>();
            for (Element possibleState : model.getElement().getEnclosedElements()) {
                State instanceState = possibleState.getAnnotation(State.class);
                if (instanceState != null) {
                    StateModel state = new StateModel(possibleState, this);
                    states.add(state);
                }
            }

            if (hasErrorOccurred()) return true;

            classBuilder.addMethod(methodAggregator.getBundlerBuildMethod(model, argList));

            if (hasErrorOccurred()) return true;

            Writer writer = Writer.from(this, model, argList, states, packageName);
            writer.addToBundler(classBuilder);

            try {
                writer.brewHelper().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            JavaFile.builder(packageName, classBuilder.build())
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<String>();

        annotations.add(Arg.class.getCanonicalName());
        annotations.add(RequireBundler.class.getCanonicalName());
        annotations.add(State.class.getCanonicalName());

        return annotations;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Types typeUtils() {
        return typeUtils;
    }

    @Override
    public Elements elementUtils() {
        return elementUtils;
    }

    @Override
    public Filer filer() {
        return filer;
    }

    @Override
    public Messager messager() {
        return messager;
    }

    @Override
    public void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    @Override
    public void message(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, args),
                e);
    }

    @Override
    public void warn(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.WARNING,
                String.format(msg, args),
                e);
    }

    @Override
    public void reportError() {
        this.errorOccurred = true;
    }

    @Override
    public boolean hasErrorOccurred() {
        return errorOccurred;
    }

    @Override
    public ClassName bundlerClass() {
        return ClassName.get("in.workarounds.bundler", "Bundler");
    }


}
