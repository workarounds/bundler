package in.workarounds.freighter.compiler.support.helper;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.util.Elements;

import in.workarounds.freighter.compiler.model.CargoModel;

/**
 * Created by madki on 22/10/15.
 */
public class ParcelableTypeVariableHelper extends TypeHelper {
    private static final ClassName SPARSE_ARRAY_CLASS = ClassName.get("android.util", "SparseArray");
    public static List<ClassName> supportedRawTypes = Arrays.asList(
            ClassName.get(ArrayList.class),
            SPARSE_ARRAY_CLASS
    );

    public ParcelableTypeVariableHelper(CargoModel cargo, Elements elementUtils) {
        super(cargo);
        if(!isKnownParcelableTypeVariable(type, elementUtils)) {
            throw new IllegalStateException("ParcelableTypeVariableHelper used for an unsupported type");
        }
    }

    @Override
    public String getBundleMethodSuffix() {
        ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) type;
        if(parameterizedTypeName.rawType.equals(ClassName.get(ArrayList.class))) {
            return "ParcelableArrayList";
        }
        if(parameterizedTypeName.rawType.equals(SPARSE_ARRAY_CLASS)) {
            return "SparseParcelableArray";
        }
        throw new IllegalStateException("Unhandled raw type in ParcelableTypeVariableHelper");
    }

    @Override
    public boolean requiresCasting() {
        return false;
    }

    public static boolean isKnownParcelableTypeVariable(TypeName typeName, Elements elementUtils) {
        if (typeName instanceof ParameterizedTypeName) {
            ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
            if (parameterizedTypeName.typeArguments.size() == 1
                    && supportedRawTypes.contains(parameterizedTypeName.rawType)) {
                return in.workarounds.freighter.compiler.support.SupportResolver.isParcelable(parameterizedTypeName.typeArguments.get(0), elementUtils);
            }
        }
        return false;
    }
}
