package in.workarounds.autorickshaw.compiler.support.helper;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.workarounds.autorickshaw.compiler.model.CargoModel;
import in.workarounds.autorickshaw.compiler.util.CommonClasses;

/**
 * Created by madki on 22/10/15.
 */
public class KnownTypeHelper extends TypeHelper {
    private static List<TypeName> knownTypes = Arrays.asList(
            CommonClasses.BUNDLE,
            TypeName.get(String.class),
            TypeName.get(CharSequence.class),
            ArrayTypeName.of(String.class),
            ArrayTypeName.of(CharSequence.class),
            ParameterizedTypeName.get(ArrayList.class, String.class),
            ParameterizedTypeName.get(ArrayList.class, CharSequence.class),
            ParameterizedTypeName.get(ArrayList.class, Integer.class)
    );

    public KnownTypeHelper(CargoModel cargo) {
        super(cargo);
        if(!isKnownType(type)) {
            throw new IllegalStateException("KnownTypeHelper used for an unrecognized type");
        }
    }

    @Override
    public String getBundleMethodSuffix() {
        if(type instanceof ClassName) {
            return ((ClassName) type).simpleName();
        }
        if(type instanceof ArrayTypeName) {
            ClassName component = (ClassName) ((ArrayTypeName) type).componentType;
            return component.simpleName() + "Array";
        }
        if(type instanceof ParameterizedTypeName) {
            ClassName rootType = ((ParameterizedTypeName) type).rawType;
            ClassName parameter = (ClassName) ((ParameterizedTypeName) type).typeArguments.get(0);
            return parameter.simpleName() + rootType.simpleName();
        }
        throw new IllegalStateException("Unhandled case in KnownTypeHelper");
    }

    @Override
    public boolean requiresCasting() {
        return false;
    }

    public static boolean isKnownType(TypeName typeName) {
        return knownTypes.contains(typeName);
    }
}
