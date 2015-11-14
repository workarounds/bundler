package in.workarounds.bundler.compiler.model;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.Required;
import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.util.Utils;

/**
 * Created by madki on 30/10/15.
 */
public class ArgModel extends AnnotatedField {
    private Element element;
    private List<AnnotationSpec> supportAnnotations;
    private Required required;

    public ArgModel(Element element, Provider provider) {
        super(element, provider, Arg.class);
        this.element = element;

        supportAnnotations = new ArrayList<>();
        for(AnnotationMirror annotationMirror: element.getAnnotationMirrors()) {
           if(Utils.isSupportAnnotation(annotationMirror)) {
               supportAnnotations.add(AnnotationSpec.get(annotationMirror));
           }
        }

        required = element.getAnnotation(Required.class);
    }

    public boolean isRequired(boolean requireAll) {
        return required == null ? requireAll : required.value();
    }

    public List<AnnotationSpec> getSupportAnnotations() {
        return supportAnnotations;
    }

    public Element getElement() {
        return element;
    }

    @Override
    public ParameterSpec getAsParameter(Modifier... modifiers) {
        return super.getAsParameter(modifiers).toBuilder()
                .addAnnotations(getSupportAnnotations())
                .build();
    }

    @Override
    public FieldSpec getAsField(Modifier... modifiers) {
        return super.getAsField(modifiers).toBuilder()
                .addAnnotations(getSupportAnnotations())
                .build();
    }
}
