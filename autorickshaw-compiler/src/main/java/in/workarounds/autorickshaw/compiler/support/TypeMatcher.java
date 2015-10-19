package in.workarounds.autorickshaw.compiler.support;

import javax.lang.model.type.TypeKind;

import in.workarounds.autorickshaw.compiler.model.PassengerModel;
import in.workarounds.autorickshaw.compiler.model.type.BasicType;
import in.workarounds.autorickshaw.compiler.model.type.RootType;
import in.workarounds.autorickshaw.compiler.support.helper.IntSupportHelper;
import in.workarounds.autorickshaw.compiler.support.helper.SupportHelper;

/**
 * Created by madki on 16/10/15.
 */
public class TypeMatcher {

    public static boolean isSupported(RootType rootType) {
        if(rootType == null) {
            return false;
        }

        if(rootType instanceof BasicType) {
            return true;
        } else {
            return false;
        }
    }

    public static SupportHelper getSupportHelper(PassengerModel passengerModel) {
        RootType rootType = passengerModel.getType();
        if(rootType == null) {
            return null;
        }

        if(rootType instanceof BasicType) {
            TypeKind kind = ((BasicType) rootType).getKind();
            if(kind == TypeKind.INT) {
                return new IntSupportHelper(passengerModel);
            }
        }

        return null;
    }
}
