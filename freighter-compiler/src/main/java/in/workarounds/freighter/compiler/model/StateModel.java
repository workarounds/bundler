package in.workarounds.freighter.compiler.model;

import javax.lang.model.element.Element;

import in.workarounds.freighter.annotations.InstanceState;
import in.workarounds.freighter.compiler.Provider;

/**
 * Created by madki on 30/10/15.
 */
public class StateModel extends AnnotatedField {

    public StateModel(Element element, Provider provider) {
        super(element, provider, InstanceState.class);
    }

}
