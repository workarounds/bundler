package in.workarounds.bundler.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.State;
import in.workarounds.bundler.compiler.generator.BundlerWriter;
import in.workarounds.bundler.compiler.generator.HelperWriter;
import in.workarounds.bundler.compiler.model.ReqBundlerModel;
import in.workarounds.bundler.compiler.util.Utils;

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

        List<ReqBundlerModel> reqBundlerModels = getModels(roundEnv);

        if (hasErrorOccurred()) return true;

        BundlerWriter bundlerWriter = new BundlerWriter(reqBundlerModels);
        bundlerWriter.checkValidity(this);

        if (hasErrorOccurred()) return true;

        if (reqBundlerModels.size() == 0) return true;

        for (ReqBundlerModel model : reqBundlerModels) {
            try {
                new HelperWriter(model).brewJava().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            bundlerWriter.brewJava().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private List<ReqBundlerModel> getModels(RoundEnvironment roundEnv) {
        HashMap<Element, ReqBundlerModel> modelMap = new HashMap<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(RequireBundler.class)) {
            if (modelMap.containsKey(element)) continue;

            retrieveElement(modelMap, element);
        }
        return new ArrayList<>(modelMap.values());
    }

    private void retrieveElement(HashMap<Element, ReqBundlerModel> modelMap, Element element) {
        RequireBundler annotation = element.getAnnotation(RequireBundler.class);
        if (!annotation.inheritArgs() && !annotation.inheritState()) {
            modelMap.put(element, new ReqBundlerModel(element, this));
        } else {
            Element superClass = getAnnotatedSuperClass(element);
            if (superClass == null) {
                modelMap.put(element, new ReqBundlerModel(element, this));
            } else {
                ReqBundlerModel superModel = modelMap.get(superClass);
                if (superModel != null) {
                    modelMap.put(element, new ReqBundlerModel(element, superModel, this));
                } else {
                    retrieveElement(modelMap, superClass);
                    modelMap.put(element, new ReqBundlerModel(element, modelMap.get(superClass), this));
                }
            }
        }
    }


    private Element getAnnotatedSuperClass(Element element) {
        Element superClass = Utils.getSuperClass(element);
        if (superClass != null) {
            RequireBundler annotation = superClass.getAnnotation(RequireBundler.class);
            if (annotation != null) {
                return superClass;
            }
            return getAnnotatedSuperClass(superClass);
        }
        return null;
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
