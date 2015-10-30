package in.workarounds.freighter.compiler;

import com.google.auto.service.AutoService;

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
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import in.workarounds.freighter.annotations.Cargo;
import in.workarounds.freighter.annotations.Freighter;
import in.workarounds.freighter.annotations.InstanceState;
import in.workarounds.freighter.compiler.generator.Writer;
import in.workarounds.freighter.compiler.model.AnnotatedField;
import in.workarounds.freighter.compiler.model.FreighterModel;

@AutoService(Processor.class)
public class FreighterProcessor extends AbstractProcessor implements Provider {

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
        for (Element element : roundEnv.getElementsAnnotatedWith(Freighter.class)) {
            FreighterModel model = new FreighterModel(element, this);
            if(hasErrorOccurred()) return true;

            List<AnnotatedField> cargoList = new ArrayList<>();
            for (Element possibleCargo : element.getEnclosedElements()) {
                Cargo cargo = possibleCargo.getAnnotation(Cargo.class);
                if (cargo != null) {
                    AnnotatedField cargoModel = new AnnotatedField(possibleCargo, this, Cargo.class);
                    cargoList.add(cargoModel);
                }
            }

            List<AnnotatedField> states = new ArrayList<>();
            for (Element possibleState : element.getEnclosedElements()) {
                InstanceState cargo = possibleState.getAnnotation(InstanceState.class);
                if (cargo != null) {
                    AnnotatedField state = new AnnotatedField(possibleState, this, InstanceState.class);
                    states.add(state);
                }
            }

            if(hasErrorOccurred()) return true;

            Writer writer = Writer.from(this, model, cargoList, states);

            try {
                writer.brewJava().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        annotations.add(Cargo.class.getCanonicalName());
        annotations.add(Freighter.class.getCanonicalName());
        annotations.add(InstanceState.class.getCanonicalName());

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


}
