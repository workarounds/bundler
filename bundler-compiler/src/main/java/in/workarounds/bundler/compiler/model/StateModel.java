package in.workarounds.bundler.compiler.model;

import com.squareup.javapoet.ClassName;

import javax.lang.model.element.Element;

import in.workarounds.bundler.annotations.State;
import in.workarounds.bundler.compiler.Provider;

/**
 * Created by madki on 30/10/15.
 */
public class StateModel extends AnnotatedField {

    public StateModel(Element element, Provider provider, ClassName serializer, String keyValue) {
        super(element, provider, State.class, serializer, keyValue);
    }

}
