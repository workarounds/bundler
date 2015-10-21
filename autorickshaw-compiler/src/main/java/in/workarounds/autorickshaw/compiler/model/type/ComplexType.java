package in.workarounds.autorickshaw.compiler.model.type;

import com.squareup.javapoet.ClassName;

import java.util.List;

/**
 * Created by madki on 17/10/15.
 */
public class ComplexType extends SimpleType {
    private final List<ClassName> parameters;
    private final boolean isParcelableArrayList;

    public ComplexType(SimpleType simpleType, List<ClassName> parameters, boolean isParcelableArrayList) {
        super(simpleType);
        this.parameters = parameters;
        this.isParcelableArrayList = isParcelableArrayList;
    }

    public ComplexType(ComplexType complexType) {
        super(complexType);
        this.parameters = complexType.getParameters();
        this.isParcelableArrayList = complexType.isParcelableArrayList();
    }

    public boolean isParcelableArrayList() {
        return isParcelableArrayList;
    }

    public List<ClassName> getParameters() {
        return parameters;
    }
}
