package in.workarounds.autorikshaw.compiler.model.type;

import com.squareup.javapoet.ClassName;

/**
 * Created by madki on 17/10/15.
 */
public class SimpleType extends RootType {
    private final ClassName primaryClass;
    private final boolean isParcelable;
    private final boolean isSerializable;

    public SimpleType(ClassName primaryClass, boolean isParcelable, boolean isSerializable, RootType rootType) {
        super(rootType);
        this.primaryClass = primaryClass;
        this.isParcelable = isParcelable;
        this.isSerializable = isSerializable;
    }

    public SimpleType(SimpleType simpleType) {
        super(simpleType);
        this.primaryClass = simpleType.getPrimaryClass();
        this.isParcelable = simpleType.isParcelable();
        this.isSerializable = simpleType.isSerializable();
    }

    public ClassName getPrimaryClass() {
        return primaryClass;
    }

    public boolean isParcelable() {
        return isParcelable;
    }

    public boolean isSerializable() {
        return isSerializable;
    }
}
