package in.workarounds.autorikshaw.compiler.support;

import com.squareup.javapoet.ClassName;

/**
 * Created by madki on 16/10/15.
 */
public class ParsedType {
    private SupportedType supportedType;
    private Class<?> primitiveType;
    private ClassName primaryType;
    private ClassName secondaryType;
    private boolean unsupported;
    private boolean list;
    private boolean array;
    private boolean parcelable;
    private boolean serializable;

    public SupportedType getSupportedType() {
        return supportedType;
    }

    @Override
    public String toString() {
        return new StringBuilder("")
                .append("supportedType = ").append(supportedType)
                .append("\n")
                .append("primitiveType = ").append(primitiveType)
                .append("\n")
                .append("primaryType = ").append(primaryType)
                .append("\n")
                .append("secondaryType = ").append(secondaryType)
                .append("\n")
                .append("unsupported = ").append(unsupported)
                .append("\n")
                .append("list = ").append(list)
                .append("\n")
                .append("array = ").append(array)
                .append("\n")
                .append("parcelable = ").append(parcelable)
                .append("\n")
                .append("serializable = ").append(serializable)
                .toString();
    }

    public void setSupportedType(SupportedType supportedType) {
        this.supportedType = supportedType;
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

    public boolean isUnsupported() {
        return unsupported;
    }

    public void setUnsupported(boolean unsupported) {
        this.unsupported = unsupported;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
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

    public Class<?> getPrimitiveType() {
        return primitiveType;
    }

    public void setPrimitiveType(Class<?> primitiveType) {
        this.primitiveType = primitiveType;
    }

    public void setSerializable(boolean serializable) {
        this.serializable = serializable;
    }
}
