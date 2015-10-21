package in.workarounds.autorickshaw.compiler.support;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.io.Serializable;

import javax.lang.model.util.Elements;

import in.workarounds.autorickshaw.compiler.model.CargoModel;
import in.workarounds.autorickshaw.compiler.support.helper.ParcelableHelper;
import in.workarounds.autorickshaw.compiler.support.helper.PrimitiveHelper;
import in.workarounds.autorickshaw.compiler.support.helper.SerializableHelper;
import in.workarounds.autorickshaw.compiler.support.helper.TypeHelper;
import in.workarounds.autorickshaw.compiler.util.Utils;

/**
 * Created by madki on 21/10/15.
 */
public class SupportResolver {

    public static boolean isSupportedType(TypeName typeName, Elements elementUtils) {
        if(typeName.isPrimitive()) {
            return true;
        }

        if(typeName instanceof ArrayTypeName) {
            TypeName componentType = ((ArrayTypeName) typeName).componentType;
            if(componentType.isPrimitive()) {
                return true;
            }
        }

        if(typeName instanceof ClassName) {
            ClassName className = (ClassName) typeName;
            return isParcelable(className, elementUtils) || isSerializable(className, elementUtils);
        }

        if(typeName instanceof ParameterizedTypeName) {

        }

        return false;
    }

    public static TypeHelper getHelper(CargoModel cargo, Elements elementUtils) {
        TypeName type = cargo.getTypeName();
        if(type.isPrimitive()) {
            return new PrimitiveHelper(cargo);
        }
        if(Utils.isPrimitiveArray(type)) {
            return new in.workarounds.autorickshaw.compiler.support.helper.PrimitiveArrayHelper(cargo);
        }
        if(type instanceof ClassName) {
            ClassName className = (ClassName) type;
            if(isSerializable(className, elementUtils)) {
                return new SerializableHelper(cargo, elementUtils);
            }
            if(isParcelable(className, elementUtils)) {
                return new ParcelableHelper(cargo, elementUtils);
            }
        }

        return null;
    }

    public static boolean isParcelable(ClassName className, Elements elementUtils) {
        return Utils.implementsInterface(
                Utils.getTypeMirror(className, elementUtils),
                "android.os.Parcelable"
        );
    }

    public static boolean isSerializable(ClassName className, Elements elementUtils) {
        return Utils.implementsInterface(
                Utils.getTypeMirror(className, elementUtils),
                Serializable.class.getCanonicalName()
        );
    }
}
