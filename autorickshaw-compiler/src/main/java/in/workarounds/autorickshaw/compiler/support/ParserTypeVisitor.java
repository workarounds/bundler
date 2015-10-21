package in.workarounds.autorickshaw.compiler.support;

import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor7;

import in.workarounds.autorickshaw.compiler.model.type.BasicType;
import in.workarounds.autorickshaw.compiler.model.type.ComplexType;
import in.workarounds.autorickshaw.compiler.model.type.RootType;
import in.workarounds.autorickshaw.compiler.model.type.SimpleType;
import in.workarounds.autorickshaw.compiler.util.Utils;

/**
 * Created by madki on 17/10/15.
 */
public class ParserTypeVisitor extends SimpleTypeVisitor7<RootType, RootType> {
    private static final String PARCELABLE_CLASS_NAME = "android.os.Parcelable";
    private static final String SERIALIZABLE_CLASS_NAME = "java.io.Serializable";
    private static final ClassName ARRAY_LIST_CLASS_NAME = ClassName.get(ArrayList.class);
    private static ParserTypeVisitor visitor;

    public static ParserTypeVisitor getInstance() {
        if (visitor == null) {
            visitor = new ParserTypeVisitor();
        }
        return visitor;
    }

    @Override
    public RootType visitPrimitive(PrimitiveType t, RootType rootType) {
        return new BasicType(rootType, t.getKind());
    }

    @Override
    public RootType visitArray(ArrayType t, RootType rootType) {
        return t.getComponentType().accept(ParserTypeVisitor.getInstance(), new RootType(true));
    }

    @Override
    public RootType visitDeclared(DeclaredType t, RootType rootType) {
        List<? extends TypeMirror> typeArguments = t.getTypeArguments();
        switch (typeArguments.size()) {
            case 0:
                return getSimpleType(t, rootType);
            case 1:
                ClassName parameter = Utils.getClassName(typeArguments.get(0));
                SimpleType primaryType = getSimpleType(t, rootType);
                if(parameter != null && primaryType != null) {
                    boolean isArrayList = primaryType.getPrimaryClass().equals(ARRAY_LIST_CLASS_NAME);
                    boolean isArrayListParcelable = isArrayList;
                    if(isArrayList) {
                        isArrayListParcelable = isParcelable(typeArguments.get(0));
                    }
                    List<ClassName> parameters = new ArrayList<>();
                    parameters.add(parameter);
                    return new ComplexType(primaryType, parameters, isArrayListParcelable);
                }
                return null;
            default:
                SimpleType originalType = getSimpleType(t, rootType);
                List<ClassName> parametricTypes = new ArrayList<>();
                for (TypeMirror typeMirror: typeArguments) {
                    ClassName type = Utils.getClassName(typeMirror);
                    if(type == null) {
                        return null;
                    }
                    parametricTypes.add(type);
                }
                if(originalType != null) {
                    return new ComplexType(originalType, parametricTypes, false);
                }
                return null;
        }
    }

    private SimpleType getSimpleType(TypeMirror typeMirror, RootType rootType) {
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
