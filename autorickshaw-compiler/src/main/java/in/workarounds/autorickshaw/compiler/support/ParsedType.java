package in.workarounds.autorickshaw.compiler.support;

import com.squareup.javapoet.ClassName;

import javax.lang.model.type.TypeKind;

/**
 * Created by madki on 16/10/15.
 */
public class ParsedType {
    private TypeKind primitiveType;
    private ClassName primaryType;
    private ClassName secondaryType;
    private boolean primitive;
    private boolean unsupported;
    private boolean parametrized;
    private boolean array;
    private boolean parcelable;
    private boolean serializable;

    @Override
    public String toString() {
        return new StringBuilder("")
                .append("primitive = ").append(primitive)
                .append("\n")
                .append("primitiveType = ").append(primitiveType)
                .append("\n")
                .append("primaryType = ").append(primaryType)
                .append("\n")
                .append("secondaryType = ").append(secondaryType)
                .append("\n")
                .append("unsupported = ").append(unsupported)
                .append("\n")
                .append("parametrized = ").append(parametrized)
                .append("\n")
                .append("array = ").append(array)
                .append("\n")
                .append("parcelable = ").append(parcelable)
                .append("\n")
                .append("serializable = ").append(serializable)
                .toString();
    }

    public TypeKind getPrimitiveType() {
        return primitiveType;
    }

    public void setPrimitiveType(TypeKind primitiveType) {
        this.primitiveType = primitiveType;
    }

    public ClassName getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(ClassName primaryType) {
        this.primaryType = primaryType;
    }

    public ClassName getSecondaryType() {
        return secondaryType;
    }

    public void setSecondaryType(ClassName secondaryType) {
        this.secondaryType = secondaryType;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    public boolean isUnsupported() {
        return unsupported;
    }

    public void setUnsupported(boolean unsupported) {
        this.unsupported = unsupported;
    }

    public boolean isParametrized() {
        return parametrized;
    }

    public void setParametrized(boolean parametrized) {
        this.parametrized = parametrized;
    }

    public boolean isArray() {
        return array;
    }

    public void setArray(boolean array) {
        this.array = array;
    }

    public boolean isParcelable() {
        return parcelable;
    }

    public void setParcelable(boolean parcelable) {
        this.parcelable = parcelable;
    }

    public boolean isSerializable() {
        return serializable;
    }

    public void setSerializable(boolean serializable) {
        this.serializable = serializable;
    }
}
