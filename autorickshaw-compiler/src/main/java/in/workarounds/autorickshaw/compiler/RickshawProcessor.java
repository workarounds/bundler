package in.workarounds.autorickshaw.compiler;

import com.google.auto.service.AutoService;

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

@AutoService(Processor.class)
public class RickshawProcessor extends AbstractProcessor {

    public Types typeUtils;
    public Elements elementUtils;
    public Filer filer;
    public Messager messager;

    public boolean errorStatus;

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
        for (Element element : roundEnv.getElementsAnnotatedWith(in.workarounds.autorickshaw.annotations.Destination.class)) {
            in.workarounds.autorickshaw.compiler.model.DestinationModel model = new in.workarounds.autorickshaw.compiler.model.DestinationModel(element, this);
            if(errorStatus) return true;

            List<in.workarounds.autorickshaw.compiler.model.PassengerModel> passengers = new ArrayList<>();
            for (Element possiblePassenger : element.getEnclosedElements()) {
                in.workarounds.autorickshaw.annotations.Passenger passenger = possiblePassenger.getAnnotation(in.workarounds.autorickshaw.annotations.Passenger.class);
                if (passenger != null) {
                    in.workarounds.autorickshaw.compiler.model.PassengerModel passengerModel = new in.workarounds.autorickshaw.compiler.model.PassengerModel(possiblePassenger, this);
                }
            }

            if(errorStatus) return true;

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

        annotations.add(in.workarounds.autorickshaw.annotations.Passenger.class.getCanonicalName());
        annotations.add(in.workarounds.autorickshaw.annotations.Destination.class.getCanonicalName());

        return annotations;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    public void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    public void message(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, args),
                e);
    }

    public void warn(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.WARNING,
                String.format(msg, args),
                e);
    }


}
