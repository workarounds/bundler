package in.workarounds.bundler.compiler.model;

import com.squareup.javapoet.AnnotationSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import in.workarounds.bundler.annotations.BundlerArg;
import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.util.Utils;

/**
 * Created by madki on 30/10/15.
 */
public class ArgModel extends AnnotatedField {
    private List<AnnotationSpec> supportAnnotations;
    private int[] required;

    public ArgModel(Element element, Provider provider) {
        super(element, provider, BundlerArg.class);

        supportAnnotations = new ArrayList<>();
        for(AnnotationMirror annotationMirror: element.getAnnotationMirrors()) {
           if(Utils.isSupportAnnotation(annotationMirror)) {
               supportAnnotations.add(AnnotationSpec.get(annotationMirror));
           }
        }

        BundlerArg annotation = element.getAnnotation(BundlerArg.class);
        required = annotation.required();
    }

    public List<AnnotationSpec> getSupportAnnotations() {
        return supportAnnotations;
    }

    public int[] getRequired() {
        return required;
    }
}
