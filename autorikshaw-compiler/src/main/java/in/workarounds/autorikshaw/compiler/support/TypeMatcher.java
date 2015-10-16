package in.workarounds.autorikshaw.compiler.support;

import java.util.Arrays;
import java.util.List;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by madki on 16/10/15.
 */
public class TypeMatcher {
    private static final String LIST_CLASS_NAME = "java.util.List";
    private static final String ARRAY_LIST_CLASS_NAME = "java.util.ArrayList";

    /**
     * Allowed Secondary types for parametric types
     */
    private static final List<String> supportedSecondaryTypes = Arrays.asList(
            LIST_CLASS_NAME.toLowerCase(),
            ARRAY_LIST_CLASS_NAME.toLowerCase()
    );

    /**
     * Primary types that are supported.
     */
    private static final List<String> supportedPrimaryTypes = Arrays.asList(
            String.class.getCanonicalName().toLowerCase(),
            Integer.class.getCanonicalName().toLowerCase(),
            Boolean.class.getCanonicalName().toLowerCase()
    );

    public static SupportedType find(TypeMirror type) {
        if (type.getKind() == TypeKind.INT) {
            return SupportedType.INT;
        } else if (type.getKind() == TypeKind.BOOLEAN) {
            return SupportedType.BOOLEAN;
        } else if (type.getKind() == TypeKind.BYTE) {
            return SupportedType.BYTE;
        } else if (type.getKind() == TypeKind.CHAR) {
            return SupportedType.CHAR;
        } else if (type.getKind() == TypeKind.DOUBLE) {
            return SupportedType.DOUBLE;
        } else if (type.getKind() == TypeKind.FLOAT) {
            return SupportedType.FLOAT;
        } else if (type.getKind() == TypeKind.LONG) {
            return SupportedType.LONG;
        } else if (type.getKind() == TypeKind.SHORT) {
            return SupportedType.SHORT;
        } else {
            return SupportedType.NONE;
        }
    }

    public static boolean finalCheck(ParsedType type) {

        return true;
    }

    public static void parsePrimitive(PrimitiveType t, ParsedType inputType) {
        inputType.setUnsupported(false);
        if (t.getKind() == TypeKind.BOOLEAN) {
            inputType.setSupportedType(
                    inputType.isArray() ?
                            SupportedType.BOOLEAN_ARRAY : SupportedType.BOOLEAN
            );
        } else if (t.getKind() == TypeKind.BYTE) {
            inputType.setSupportedType(
                    inputType.isArray() ?
                            SupportedType.BYTE_ARRAY : SupportedType.BYTE
            );
        } else if (t.getKind() == TypeKind.CHAR) {
            inputType.setSupportedType(
                    inputType.isArray() ?
                            SupportedType.CHAR_ARRAY : SupportedType.CHAR
            );
        } else if (t.getKind() == TypeKind.DOUBLE) {
            inputType.setSupportedType(
                    inputType.isArray() ?
                            SupportedType.DOUBLE_ARRAY : SupportedType.DOUBLE
            );
        } else if (t.getKind() == TypeKind.FLOAT) {
            inputType.setSupportedType(
                    inputType.isArray() ?
                            SupportedType.FLOAT_ARRAY : SupportedType.FLOAT
            );
        } else if (t.getKind() == TypeKind.INT) {
            inputType.setSupportedType(
                    inputType.isArray() ?
                            SupportedType.INT_ARRAY : SupportedType.INT
            );
        } else if (t.getKind() == TypeKind.LONG) {
            inputType.setSupportedType(
                    inputType.isArray() ?
                            SupportedType.LONG_ARRAY : SupportedType.LONG
            );
        } else {
            inputType.setUnsupported(true);
        }
    }

    public static boolean isSupportedSecondaryType(String qualifiedName) {
        return qualifiedName != null &&
                supportedSecondaryTypes.contains(qualifiedName.toLowerCase());
    }

    public static boolean isSupportedPrimaryType(String qualifiedName) {
        return qualifiedName != null &&
                supportedPrimaryTypes.contains(qualifiedName.toLowerCase());
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
