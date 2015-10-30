package in.workarounds.freighter.compiler.model;

import com.squareup.javapoet.AnnotationSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import in.workarounds.freighter.annotations.Cargo;
import in.workarounds.freighter.compiler.Provider;
import in.workarounds.freighter.compiler.util.Utils;

/**
 * Created by madki on 30/10/15.
 */
public class CargoModel extends AnnotatedField {
    private List<AnnotationSpec> supportAnnotations;

    public CargoModel(Element element, Provider provider) {
        super(element, provider, Cargo.class);

        supportAnnotations = new ArrayList<>();
        for(AnnotationMirror annotationMirror: element.getAnnotationMirrors()) {
           if(Utils.isSupportAnnotation(annotationMirror.getAnnotationType())) {
               supportAnnotations.add(AnnotationSpec.get(annotationMirror));
           }
        }
    }

    public List<AnnotationSpec> getSupportAnnotations() {
        return supportAnnotations;
    }
}
