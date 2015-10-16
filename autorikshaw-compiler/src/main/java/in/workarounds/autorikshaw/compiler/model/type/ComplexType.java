package in.workarounds.autorikshaw.compiler.model.type;

import com.squareup.javapoet.ClassName;

/**
 * Created by madki on 17/10/15.
 */
public class ComplexType extends SimpleType {
    private final ClassName secondaryClass;

    public ComplexType(SimpleType simpleType, ClassName secondaryClass) {
        super(simpleType);
        this.secondaryClass = secondaryClass;
    }

    public ComplexType(ComplexType complexType) {
        super(complexType);
        this.secondaryClass = complexType.getSecondaryClass();
    }

    public ClassName getSecondaryClass() {
        return secondaryClass;
    }
}
