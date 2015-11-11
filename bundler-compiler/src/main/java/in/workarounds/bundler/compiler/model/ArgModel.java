package in.workarounds.bundler.compiler.model;

import com.squareup.javapoet.AnnotationSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.util.Utils;

/**
 * Created by madki on 30/10/15.
 */
public class ArgModel extends AnnotatedField {
    private List<AnnotationSpec> supportAnnotations;
    private int[] required;

    public ArgModel(Element element, Provider provider) {
        super(element, provider, Arg.class);

        supportAnnotations = new ArrayList<>();
        for(AnnotationMirror annotationMirror: element.getAnnotationMirrors()) {
           if(Utils.isSupportAnnotation(annotationMirror)) {
               supportAnnotations.add(AnnotationSpec.get(annotationMirror));
           }
        }

        Arg annotation = element.getAnnotation(Arg.class);
        required = annotation.required();
    }

    public List<AnnotationSpec> getSupportAnnotations() {
        return supportAnnotations;
    }

    public int[] getRequired() {
        return required;
    }
}
