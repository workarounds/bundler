package in.workarounds.autorikshaw.compiler.support;

import com.squareup.javapoet.ClassName;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleTypeVisitor7;

/**
 * Created by madki on 16/10/15.
 */
public class CustomTypeVisitor extends SimpleTypeVisitor7<ParsedType, ParsedType> {
    private static CustomTypeVisitor typeVisitor;
    private static final String PARCELABLE_CLASS_NAME = "android.os.Parcelable";
    private static final String SERIALIZABLE_CLASS_NAME = "java.io.Serializable";

    public static CustomTypeVisitor getInstance() {
        if (typeVisitor == null) {
            typeVisitor = new CustomTypeVisitor();
        }
        return typeVisitor;
    }

    @Override
    public ParsedType visitArray(ArrayType t, ParsedType inputType) {
        inputType.setArray(true);
        t.getComponentType().accept(CustomTypeVisitor.getInstance(), inputType);
        return inputType;
    }

    @Override
    public ParsedType visitDeclared(DeclaredType t, ParsedType inputType) {
        if (t.getTypeArguments().size() == 0) {
            boolean parcelable = isParcelable(t);
            boolean serializable = isSerializable(t);
            inputType.setParcelable(parcelable);
            inputType.setSerializable(serializable);
            inputType.setPrimaryType(ClassName.bestGuess(
                    ((TypeElement) t.asElement())
                            .getQualifiedName()
                            .toString()));
        } else if (t.getTypeArguments().size() == 1) {
            String secondaryClass = ((TypeElement) t.asElement())
                    .getQualifiedName()
                    .toString();
            inputType.setSecondaryType(ClassName.bestGuess(secondaryClass));
            if (TypeMatcher.isSupportedSecondaryType(secondaryClass)) {
                inputType.setParametrized(true);
                t.getTypeArguments().get(0).accept(CustomTypeVisitor.getInstance(), inputType);
            } else {
                // TODO Currently supporting parametric types with only one parameter and that too only lists
                inputType.setUnsupported(true);
            }
        } else {
            // TODO Currently supporting parametric types with only one parameter and that too only lists
            inputType.setUnsupported(true);
        }

        return inputType;
    }

    @Override
    public ParsedType visitError(ErrorType t, ParsedType inputType) {
        inputType.setUnsupported(true);
        return inputType;
    }

    @Override
    public ParsedType visitExecutable(ExecutableType t, ParsedType inputType) {
        inputType.setUnsupported(true);
        return inputType;
    }

    @Override
    public ParsedType visitNoType(NoType t, ParsedType inputType) {
        inputType.setUnsupported(true);
        return inputType;
    }

    @Override
    public ParsedType visitNull(NullType t, ParsedType inputType) {
        inputType.setUnsupported(true);
        return inputType;
    }

    @Override
    public ParsedType visitPrimitive(PrimitiveType t, ParsedType inputType) {
        TypeMatcher.parsePrimitive(t, inputType);
        return inputType;
    }

    @Override
    public ParsedType visitTypeVariable(TypeVariable t, ParsedType inputType) {
        inputType.setUnsupported(true);
        return inputType;
    }

    @Override
    public ParsedType visitUnion(UnionType t, ParsedType inputType) {
        inputType.setUnsupported(true);
        return inputType;
    }

    @Override
    public ParsedType visitUnknown(TypeMirror t, ParsedType inputType) {
        inputType.setUnsupported(true);
        return inputType;
    }

    @Override
    public ParsedType visitWildcard(WildcardType t, ParsedType inputType) {
        inputType.setUnsupported(true);
        return inputType;
    }

    private boolean implementsInterface(TypeMirror typeMirror, String interfaceName) {
        if (typeMirror.getKind() == TypeKind.DECLARED) {
            TypeElement type = (TypeElement) ((DeclaredType) typeMirror).asElement();
            List<? extends TypeMirror> interfaces = type.getInterfaces();
            for (TypeMirror t : interfaces) {
                if (isSameInterface(t, interfaceName)) {
                    return true;
                }
            }
            return implementsInterface(type.getSuperclass(), interfaceName);
        }
        return false;

    }

    private boolean isParcelable(TypeMirror typeMirror) {
        return implementsInterface(typeMirror, PARCELABLE_CLASS_NAME);
    }

    private boolean isSerializable(TypeMirror typeMirror) {
        return implementsInterface(typeMirror, SERIALIZABLE_CLASS_NAME);
    }

    private boolean isSameInterface(TypeMirror t, String qualifiedName) {
        if (t.getKind() == TypeKind.DECLARED) {
            TypeElement typeElement = (TypeElement) ((DeclaredType) t).asElement();
            if (typeElement != null) {
                if (typeElement.getQualifiedName().contentEquals(qualifiedName)) {
                    return true;
                } else {
                    return isSameInterface(typeElement.getSuperclass(), qualifiedName);
                }
            }
        }
        return false;
    }
}
