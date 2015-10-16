package in.workarounds.autorickshaw.compiler.support;

import com.squareup.javapoet.ClassName;

import java.util.List;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor7;

import in.workarounds.autorickshaw.compiler.model.type.BasicType;
import in.workarounds.autorickshaw.compiler.model.type.ComplexType;
import in.workarounds.autorickshaw.compiler.model.type.SimpleType;
import in.workarounds.autorickshaw.compiler.util.Utils;

/**
 * Created by madki on 17/10/15.
 */
public class ParserTypeVisitor extends SimpleTypeVisitor7<in.workarounds.autorickshaw.compiler.model.type.RootType, in.workarounds.autorickshaw.compiler.model.type.RootType> {
    private static final String PARCELABLE_CLASS_NAME = "android.os.Parcelable";
    private static final String SERIALIZABLE_CLASS_NAME = "java.io.Serializable";
    private static ParserTypeVisitor visitor;

    public static ParserTypeVisitor getInstance() {
        if (visitor == null) {
            visitor = new ParserTypeVisitor();
        }
        return visitor;
    }

    @Override
    public in.workarounds.autorickshaw.compiler.model.type.RootType visitPrimitive(PrimitiveType t, in.workarounds.autorickshaw.compiler.model.type.RootType rootType) {
        return new BasicType(rootType, t.getKind());
    }

    @Override
    public in.workarounds.autorickshaw.compiler.model.type.RootType visitArray(ArrayType t, in.workarounds.autorickshaw.compiler.model.type.RootType rootType) {
        return t.getComponentType().accept(ParserTypeVisitor.getInstance(), new in.workarounds.autorickshaw.compiler.model.type.RootType(true));
    }

    @Override
    public in.workarounds.autorickshaw.compiler.model.type.RootType visitDeclared(DeclaredType t, in.workarounds.autorickshaw.compiler.model.type.RootType rootType) {
        List<? extends TypeMirror> typeArguments = t.getTypeArguments();
        switch (typeArguments.size()) {
            case 0:
                return getSimpleType(t, rootType);
            case 1:
                String secondaryClass = Utils.getQualifiedName(t);
                SimpleType primaryType = getSimpleType(typeArguments.get(0), rootType);
                if(secondaryClass != null && primaryType != null) {
                    return new ComplexType(primaryType, ClassName.bestGuess(secondaryClass));
                }
                return null;
            default:
                return null;
        }
    }

    private SimpleType getSimpleType(TypeMirror typeMirror, in.workarounds.autorickshaw.compiler.model.type.RootType rootType) {
        boolean isParcelable = isParcelable(typeMirror);
        boolean isSerializable = isSerializable(typeMirror);
        String className = Utils.getQualifiedName(typeMirror);
        if(className != null) {
            return new SimpleType(ClassName.bestGuess(className), isParcelable, isSerializable, rootType);
        }
        return null;
    }

    private boolean isParcelable(TypeMirror typeMirror) {
        return Utils.implementsInterface(typeMirror, PARCELABLE_CLASS_NAME);
    }

    private boolean isSerializable(TypeMirror typeMirror) {
        return Utils.implementsInterface(typeMirror, SERIALIZABLE_CLASS_NAME);
    }
}
