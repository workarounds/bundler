package in.workarounds.freighter.compiler.support;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

import java.io.Serializable;

import javax.lang.model.util.Elements;

import in.workarounds.freighter.compiler.model.CargoModel;
import in.workarounds.freighter.compiler.support.helper.KnownTypeHelper;
import in.workarounds.freighter.compiler.support.helper.ParcelableArrayHelper;
import in.workarounds.freighter.compiler.support.helper.ParcelableHelper;
import in.workarounds.freighter.compiler.support.helper.ParcelableTypeVariableHelper;
import in.workarounds.freighter.compiler.support.helper.PrimitiveArrayHelper;
import in.workarounds.freighter.compiler.support.helper.PrimitiveHelper;
import in.workarounds.freighter.compiler.support.helper.SerializableHelper;
import in.workarounds.freighter.compiler.support.helper.TypeHelper;
import in.workarounds.freighter.compiler.util.Utils;

/**
 * Created by madki on 21/10/15.
 */
public class SupportResolver {

    public static boolean isSupportedType(CargoModel cargo, Elements elementUtils) {
        return getHelper(cargo, elementUtils) != null;
    }

    public static TypeHelper getHelper(CargoModel cargo, Elements elementUtils) {
        TypeName type = cargo.getTypeName();
        if (type.isPrimitive()) {
            return new PrimitiveHelper(cargo);
        }

        if (KnownTypeHelper.isKnownType(type)) {
            return new KnownTypeHelper(cargo);
        }

        if (isParcelable(type, elementUtils)) {
            return new ParcelableHelper(cargo, elementUtils);
        }

        if (PrimitiveArrayHelper.isPrimitiveArray(type)) {
            return new PrimitiveArrayHelper(cargo);
        }

        if (ParcelableArrayHelper.isParcelableArray(type, elementUtils)) {
            return new ParcelableArrayHelper(cargo, elementUtils);
        }

        if (ParcelableTypeVariableHelper.isKnownParcelableTypeVariable(type, elementUtils)) {
            return new ParcelableTypeVariableHelper(cargo, elementUtils);
        }

        if (isSerializable(type, elementUtils)) {
            return new SerializableHelper(cargo, elementUtils);
        }
        return null;
    }

    public static boolean isParcelable(ClassName className, Elements elementUtils) {
        return Utils.implementsInterface(
                Utils.getTypeMirror(className, elementUtils),
                "android.os.Parcelable"
        );
    }

    public static boolean isParcelable(TypeName typeName, Elements elements) {
        if (typeName instanceof WildcardTypeName
                || typeName instanceof TypeVariableName
                || typeName instanceof ArrayTypeName
                || typeName.isPrimitive()) {
            // TODO handle these better. Check is the assumption is valid
            return false;
        }
        if (typeName instanceof ClassName) {
            return isParcelable((ClassName) typeName, elements);
        }
        if (typeName instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
            ClassName primaryType = parameterizedTypeName.rawType;
            return isParcelable(primaryType, elements);
        }
        return false;
    }

    public static boolean isSerializable(TypeName typeName, Elements elements) {
        if (typeName.isPrimitive()) {
            return false;
        }
        if (typeName instanceof WildcardTypeName || typeName instanceof TypeVariableName) {
            // TODO this is definitely not true. But WildcardType fields is probably not possible. Check.
            return false;
        }
        if (typeName instanceof ArrayTypeName) {
            return isSerializable(((ArrayTypeName) typeName).componentType, elements);
        }
        if (typeName instanceof ClassName) {
            return isSerializable((ClassName) typeName, elements);
        }
        if (typeName instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
            ClassName primaryType = parameterizedTypeName.rawType;
            return isSerializable(primaryType, elements);
        }
        return false;
    }

    public static boolean isSerializable(ClassName className, Elements elementUtils) {
        return Utils.implementsInterface(
                Utils.getTypeMirror(className, elementUtils),
                Serializable.class.getCanonicalName()
        );
    }
}
