package in.workarounds.autorickshaw.compiler.model.type;

import javax.lang.model.type.TypeKind;

/**
 * Created by madki on 17/10/15.
 */
public class BasicType extends RootType {
    private final TypeKind kind;

    public BasicType(RootType type, TypeKind kind) {
        super(type);
        this.kind = kind;
    }

    public BasicType(BasicType basicType) {
        super(basicType);
        this.kind = basicType.getKind();
    }

    public TypeKind getKind() {
        return kind;
    }
}
