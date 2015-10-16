package in.workarounds.autorikshaw.compiler.support;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by madki on 16/10/15.
 */
public class TypeMatcher {

    public static SupportedType find(TypeMirror type) {
        if(type.getKind() == TypeKind.INT) {
            return SupportedType.INT;
        } else if(type.getKind() == TypeKind.BOOLEAN) {
            return SupportedType.BOOLEAN;
        } else if(type.getKind() == TypeKind.BYTE) {
            return SupportedType.BYTE;
        } else if(type.getKind() == TypeKind.CHAR) {
            return SupportedType.CHAR;
        } else if(type.getKind() == TypeKind.DOUBLE) {
            return SupportedType.DOUBLE;
        } else if(type.getKind() == TypeKind.FLOAT) {
            return SupportedType.FLOAT;
        } else if(type.getKind() == TypeKind.LONG) {
            return SupportedType.LONG;
        } else if(type.getKind() == TypeKind.SHORT) {
            return SupportedType.SHORT;
        } else {
            return SupportedType.NONE;
        }
    }

    public static boolean isTypeSupported(TypeMirror type) {
        return find(type) != SupportedType.NONE;
    }

    public static SupportHelper getSupportHelper(SupportedType type) {
        switch (type) {
            case INT:
                return new IntSupportHelper();
            default:
                return null;
        }
    }

}
